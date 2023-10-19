package com.capstone.workspace.services.order;

import com.capstone.workspace.entities.order.Order;
import com.capstone.workspace.entities.order.Transaction;
import com.capstone.workspace.enums.order.OrderStatus;
import com.capstone.workspace.enums.order.TransactionMethod;
import com.capstone.workspace.enums.order.TransactionStatus;
import com.capstone.workspace.enums.order.TransactionType;
import com.capstone.workspace.exceptions.BadRequestException;
import com.capstone.workspace.repositories.order.OrderRepository;
import com.capstone.workspace.repositories.order.TransactionRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
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
        Map cbData = objectMapper.convertValue(jsonStr, HashMap.class);
        String dataStr = (String) cbData.get("data");
        Map data = objectMapper.convertValue(dataStr, HashMap.class);

        Map<String, Object> result = zaloPayService.receiveCallback(jsonStr);

        if ((int) result.get("return_code") == 1) {
            String appTransId = (String) data.get("app_trans_id");
            Transaction transaction = repository.getByExternalPaymentInfo("appTransId", appTransId);
            transaction.setStatus(TransactionStatus.SUCCESS);

            Map<String, Object> externalPaymentInfo = transaction.getExternalPaymentInfo();
            externalPaymentInfo.put("amount", data.get("amount"));
            externalPaymentInfo.put("zpTransId", data.get("zp_trans_id"));
            externalPaymentInfo.put("serverTime", data.get("server_time"));
            externalPaymentInfo.put("merchantUserId", data.get("merchant_user_id"));
            externalPaymentInfo.put("userFeeAmount", data.get("user_fee_amount"));
            externalPaymentInfo.put("discountAmount", data.get("discount_amount"));

            transaction.setExternalPaymentInfo(externalPaymentInfo);
            repository.save(transaction);

            Order order = transaction.getOrder();
            order.setStatus(OrderStatus.ORDERED);
            orderRepository.save(order);
        }

        return objectMapper.writeValueAsString(result);
    }
}
