package com.capstone.workspace.services.order;

import com.capstone.workspace.entities.order.Order;
import com.capstone.workspace.entities.order.Transaction;
import com.capstone.workspace.enums.order.OrderStatus;
import com.capstone.workspace.enums.order.TransactionMethod;
import com.capstone.workspace.enums.order.TransactionStatus;
import com.capstone.workspace.enums.order.TransactionType;
import com.capstone.workspace.enums.user.UserType;
import com.capstone.workspace.exceptions.BadRequestException;
import com.capstone.workspace.exceptions.ConflictException;
import com.capstone.workspace.exceptions.NotFoundException;
import com.capstone.workspace.models.auth.UserIdentity;
import com.capstone.workspace.models.order.OrderModel;
import com.capstone.workspace.models.order.ZaloPayCallbackData;
import com.capstone.workspace.models.order.ZaloPayCallbackResult;
import com.capstone.workspace.repositories.order.OrderRepository;
import com.capstone.workspace.repositories.order.TransactionRepository;
import com.capstone.workspace.services.auth.IdentityService;
import com.capstone.workspace.services.shared.JobService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private static Logger logger = LoggerFactory.getLogger(TransactionService.class);

    @NonNull
    private final TransactionRepository repository;

    @NonNull
    private final ZaloPayService zaloPayService;

    @NonNull
    private final ObjectMapper objectMapper;

    @NonNull
    private final OrderRepository orderRepository;

    @NonNull
    private final JobService jobService;

    @NonNull
    private final ModelMapper mapper;

    @NonNull
    private final IdentityService identityService;

    @Transactional
    public Transaction createInitialTransaction(Order order) {
        Transaction entity = new Transaction();

        entity.setOrder(order);
        entity.setStoreId(order.getStoreId());
        entity.setPartnerId(order.getPartnerId());
        entity.setStatus(TransactionStatus.PENDING);
        entity.setAmount(order.getFinalPrice());
        entity.setType(TransactionType.PURCHASE);

        switch (order.getInitialTransactionMethod()) {
            case CASH:
                entity.setMethod(TransactionMethod.CASH);
                break;
            case ZALO_PAY:
                entity.setMethod(TransactionMethod.ZALO_PAY);
                Map res = zaloPayService.createOrder(order);

                Map<String, Object> externalPaymentInfo = new HashMap<>(){{
                    put("zpTransToken", res.get("zp_trans_token"));
                    put("orderUrl", res.get("order_url"));
                    put("appId", res.get("app_id"));
                    put("appTransId", res.get("app_trans_id"));
                }};

                entity.setExternalPaymentInfo(externalPaymentInfo);
                break;
            default:
                throw new BadRequestException("Unsupported transaction method");
        }

        return repository.save(entity);
    }

    @Transactional
    public String receiveZaloPayCallback(String jsonStr) throws JsonProcessingException {
        ZaloPayCallbackResult cbData = objectMapper.readValue(jsonStr, ZaloPayCallbackResult.class);
        String dataStr = cbData.getData();
        ZaloPayCallbackData data = objectMapper.readValue(dataStr, ZaloPayCallbackData.class);

        Map<String, Object> result = zaloPayService.receiveCallback(jsonStr);

        if ((int) result.get("return_code") == 1) {
            String appTransId = data.getApp_trans_id();
            Transaction transaction = repository.getByExternalPaymentInfo("appTransId", appTransId);
            transaction.setStatus(TransactionStatus.SUCCESS);

            Map<String, Object> externalPaymentInfo = transaction.getExternalPaymentInfo();
            externalPaymentInfo.put("amount", data.getAmount());
            externalPaymentInfo.put("zpTransId", data.getZp_trans_id());
            externalPaymentInfo.put("serverTime", data.getServer_time());
            externalPaymentInfo.put("merchantUserId", data.getMerchant_user_id());
            externalPaymentInfo.put("userFeeAmount", data.getUser_fee_amount());
            externalPaymentInfo.put("discountAmount", data.getDiscount_amount());

            transaction.setExternalPaymentInfo(externalPaymentInfo);
            repository.save(transaction);

            Order order = transaction.getOrder();
            order.setStatus(OrderStatus.ORDERED);
            orderRepository.save(order);
            jobService.publishPushNotificationOrderChangesJob(mapper.map(order, OrderModel.class));
        }

        return objectMapper.writeValueAsString(result);
    }

    public int checkTransactionStatusCode(Transaction transaction) {
        if (transaction.getMethod() == TransactionMethod.CASH) {
            throw new BadRequestException("Do not support cash transaction");
        }
        return zaloPayService.checkTransactionStatusCode(transaction);
    }

    public Transaction pay(UUID id) {
        Transaction entity = getOneById(id);

        if (entity.getMethod() != TransactionMethod.CASH) {
            throw new ConflictException("Support for cash transaction only");
        }

        if (entity.getStatus() != TransactionStatus.PENDING) {
            throw new ConflictException("Transaction has end");
        }

        entity.setStatus(TransactionStatus.SUCCESS);
        return repository.save(entity);
    }

    public Transaction getOneById(UUID id) {
        UserIdentity userIdentity = identityService.getUserIdentity();
        UserType userType = userIdentity.getUserType();

        Transaction entity = repository.findById(id).orElse(null);

        if (entity == null
                || (userType == UserType.CUSTOMER && !userIdentity.getUsername().equals(entity.getCreatedBy()))
                || (List.of(UserType.OWNER, UserType.STORE_MANAGER, UserType.STAFF).contains(userType) && (!entity.getPartnerId().equals(userIdentity.getPartnerId()) || (userIdentity.getStoreId() != null && !userIdentity.getStoreId().equals(entity.getStoreId()))))
        ) {
            throw new NotFoundException("Transaction not found");
        }

        return entity;
    }
}
