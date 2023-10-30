package com.capstone.workspace.services.order;

import com.capstone.workspace.dtos.order.CreateOrderDto;
import com.capstone.workspace.dtos.order.SearchOrderCriteriaDto;
import com.capstone.workspace.dtos.order.SearchOrderDto;
import com.capstone.workspace.entities.order.Order;
import com.capstone.workspace.entities.order.Transaction;
import com.capstone.workspace.entities.store.QrCode;
import com.capstone.workspace.enums.order.OrderEvent;
import com.capstone.workspace.enums.order.OrderStatus;
import com.capstone.workspace.enums.order.TransactionMethod;
import com.capstone.workspace.enums.order.TransactionStatus;
import com.capstone.workspace.enums.user.UserType;
import com.capstone.workspace.exceptions.BadRequestException;
import com.capstone.workspace.exceptions.ConflictException;
import com.capstone.workspace.exceptions.GoneException;
import com.capstone.workspace.exceptions.NotFoundException;
import com.capstone.workspace.helpers.shared.AppHelper;
import com.capstone.workspace.models.auth.UserIdentity;
import com.capstone.workspace.models.cart.CartDetailsModel;
import com.capstone.workspace.models.order.OrderModel;
import com.capstone.workspace.models.order.TransactionModel;
import com.capstone.workspace.models.shared.PaginationResponseModel;
import com.capstone.workspace.models.store.QrCodeModel;
import com.capstone.workspace.models.store.StoreModel;
import com.capstone.workspace.repositories.order.OrderRepository;
import com.capstone.workspace.repositories.order.TransactionRepository;
import com.capstone.workspace.services.auth.IdentityService;
import com.capstone.workspace.services.cart.CartService;
import com.capstone.workspace.services.shared.JobService;
import com.capstone.workspace.services.store.QrCodeService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {
    private static Logger logger = LoggerFactory.getLogger(OrderService.class);

    private static final int MAX_ALLOWED_ACTIVE_ORDERS = 3;

    private static final OrderStatus[] ACTIVE_ORDER_STATUSES = new OrderStatus[]{OrderStatus.ORDERED, OrderStatus.PENDING, OrderStatus.PROCESSING};

    @NonNull
    private final OrderRepository repository;

    @NonNull
    private final CartService cartService;

    @NonNull
    private final IdentityService identityService;

    @NonNull
    private final QrCodeService qrCodeService;

    @NonNull
    private final TransactionService transactionService;

    @NonNull
    private final ModelMapper mapper;

    @NonNull
    private final OrderStateMachine orderStateMachine;

    @NonNull
    private final TransactionRepository transactionRepository;

    @NonNull
    private final JobService jobService;

    @Transactional
    public OrderModel create(CreateOrderDto dto) {
        validateActiveOrderOfUser();

        UUID cartId = dto.getCartId();
        CartDetailsModel cartDetails = cartService.getCartDetails(cartId);

        UUID qrId = dto.getQrId();
        String code = dto.getQrCode();
        QrCode qrCode = null;

        if (qrId == null && code == null) {
            throw new BadRequestException("Missing QR information");
        } else if (qrId != null) {
            qrCode = qrCodeService.getOneById(qrId);
        } else {
            qrCode = qrCodeService.getOneByCode(code);
        }

        QrCodeModel qrCodeModel = mapper.map(qrCode, QrCodeModel.class);

        Order entity = new Order();
        entity.setDetails(cartDetails);
        entity.setQrCode(qrCodeModel);
        entity.setInitialTransactionMethod(dto.getInitialTransactionMethod());

        StoreModel store = cartDetails.getStore();
        if (!qrCode.getStoreId().equals(String.valueOf(store.getId()))) {
            throw new ConflictException("QR Code does not belong to this store");
        }
        if (!Boolean.TRUE.equals(store.getIsActive()) || !Boolean.TRUE.equals(store.getIsOpen())) {
            throw new GoneException("Store is unavailable now");
        }

        entity.setStoreId(String.valueOf(store.getId()));
        entity.setPartnerId(store.getPartnerId());
        entity.setFinalPrice(cartDetails.getFinalPrice());

        if (entity.getInitialTransactionMethod() == TransactionMethod.CASH) {
            entity.setStatus(OrderStatus.ORDERED);
        } else if (entity.getInitialTransactionMethod() == TransactionMethod.ZALO_PAY) {
            entity.setStatus(OrderStatus.PENDING);
        }

        cartService.deleteCart(cartId);

        Order saved = repository.save(entity);
        OrderModel orderModel = mapper.map(saved, OrderModel.class);

        Transaction transaction = transactionService.createInitialTransaction(saved);
        TransactionModel transactionModel = mapper.map(transaction, TransactionModel.class);
        orderModel.setLatestTransaction(transactionModel);

        return orderModel;
    }

    public Order getOneById(UUID id) {
        UserIdentity userIdentity = identityService.getUserIdentity();
        String username = userIdentity.getUsername();
        UserType userType = userIdentity.getUserType();

        Order entity = repository.findById(id).orElse(null);

        if (
            entity == null
            || (userType == UserType.CUSTOMER && !username.equals(entity.getCreatedBy()))
            || (userType == UserType.EMPLOYEE && (!entity.getPartnerId().equals(userIdentity.getPartnerId()) || (userIdentity.getStoreId() != null && !userIdentity.getStoreId().equals(entity.getStoreId()))))
        ) {
            throw new NotFoundException("Order not found");
        }

        return entity;
    }

    private void validateActiveOrderOfUser() {
        UserIdentity userIdentity = identityService.getUserIdentity();
        String username = userIdentity.getUsername();

        List<Order> orders = repository.findByCreatedByAndStatusIn(username, ACTIVE_ORDER_STATUSES);
        if (orders.size() >= MAX_ALLOWED_ACTIVE_ORDERS) {
            throw new BadRequestException("Exceeded max allowed active orders");
        }
    }

    public String receiveZaloPayCallback(String jsonStr) throws JsonProcessingException {
        return transactionService.receiveZaloPayCallback(jsonStr);
    }

    @Transactional
    public synchronized Order transition(UUID id, String event) {
        Order entity = getOneById(id);
        OrderEvent orderEvent = OrderEvent.valueOf(event.toUpperCase());

        orderStateMachine.init(id);

        OrderStatus newStatus = orderStateMachine.transition(entity.getStatus(), orderEvent);
        entity.setStatus(newStatus);

        switch (newStatus) {
            case DECLINED:
                // TODO: Xử lí bom hàng
                break;
            case REJECTED, CANCELED:
                // TODO: Xử lí Cashback, change transaction status khi hủy lúc PENDING
                break;
            case RECEIVED:
                Transaction transaction = transactionRepository.findByOrder_IdOrderByCreatedAtDesc(id);
                if (transaction.getStatus() != TransactionStatus.SUCCESS) {
                    throw new BadRequestException("Transaction is not completed yet");
                }
                break;
            default:
                break;
        }

        return repository.save(entity);
    }

    public PaginationResponseModel<OrderModel> search(SearchOrderDto dto) {
        String[] searchableFields = new String[]{};
        Map<String, Object> filterParams = Collections.emptyMap();

        SearchOrderCriteriaDto criteria = dto.getCriteria();
        String keyword = null;
        Map orderCriteria = null;

        if (criteria != null) {
            if (criteria.getFilter() != null) {
                filterParams = AppHelper.copyPropertiesToMap(criteria.getFilter());
            }
            keyword = criteria.getKeyword();
            orderCriteria = criteria.getOrder();
        }

        PaginationResponseModel result = repository.searchBy(
                keyword,
                searchableFields,
                filterParams,
                orderCriteria,
                dto.getPagination()
        );

        List<OrderModel> orderModels = mapper.map(
                result.getResults(),
                new TypeToken<List<OrderModel>>() {}.getType()
        );
        result.setResults(orderModels);

        return result;
    }
}
