package com.capstone.workspace.controllers.order;

import com.capstone.workspace.annotations.AllowedUsers;
import com.capstone.workspace.dtos.order.CreateOrderDto;
import com.capstone.workspace.dtos.order.RejectOrderDto;
import com.capstone.workspace.dtos.order.SearchOrderDto;
import com.capstone.workspace.entities.order.Order;
import com.capstone.workspace.entities.order.Transaction;
import com.capstone.workspace.enums.user.UserType;
import com.capstone.workspace.models.order.OrderDetailsModel;
import com.capstone.workspace.models.order.OrderModel;
import com.capstone.workspace.models.order.TransactionModel;
import com.capstone.workspace.models.shared.PaginationResponseModel;
import com.capstone.workspace.models.shared.ResponseModel;
import com.capstone.workspace.services.order.OrderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping( "/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private static Logger logger = LoggerFactory.getLogger(OrderController.class);

    @NonNull
    private final OrderService orderService;

    @NonNull
    private final ModelMapper mapper;

    @AllowedUsers(userTypes = {UserType.CUSTOMER})
    @PostMapping
    public ResponseModel<OrderModel> createOrder(@Valid @RequestBody CreateOrderDto dto) {
        OrderModel model = orderService.create(dto);
        return ResponseModel.<OrderModel>builder().data(model).build();
    }

    @GetMapping("/{id}")
    public ResponseModel<OrderDetailsModel> getOrderById(@PathVariable UUID id) {
        Order entity = orderService.getOneById(id);
        OrderDetailsModel model = mapper.map(entity, OrderDetailsModel.class);
        return ResponseModel.<OrderDetailsModel>builder().data(model).build();
    }

    @PostMapping("/zalopay/callback")
    public String receiveZaloPayOrder(@RequestBody String jsonStr) throws JsonProcessingException {
        logger.info("ZaloPay callback called");
        return orderService.receiveZaloPayCallback(jsonStr);
    }

    @PutMapping("/{id}/events/reject")
    public ResponseModel<OrderModel> reject(@PathVariable UUID id, @Valid @RequestBody RejectOrderDto dto) {
        Order entity = orderService.transition(id, "reject", dto);
        OrderModel model = mapper.map(entity, OrderModel.class);
        return ResponseModel.<OrderModel>builder().data(model).build();
    }

    @PutMapping("/{id}/events/{event}")
    public ResponseModel<OrderModel> transition(@PathVariable UUID id, @PathVariable String event) {
        Order entity = orderService.transition(id, event, null);
        OrderModel model = mapper.map(entity, OrderModel.class);
        return ResponseModel.<OrderModel>builder().data(model).build();
    }

    @PostMapping("/search")
    public ResponseModel<PaginationResponseModel<OrderModel>> search(@Valid @RequestBody SearchOrderDto dto) {
        PaginationResponseModel<OrderModel> data = orderService.search(dto);
        return ResponseModel.<PaginationResponseModel<OrderModel>>builder().data(data).build();
    }

    @AllowedUsers(userTypes = {UserType.CUSTOMER, UserType.OWNER, UserType.STORE_MANAGER, UserType.STAFF})
    @PostMapping("/{id}/transactions")
    public ResponseModel<TransactionModel> requestTransaction(@PathVariable UUID id) {
        Transaction entity = orderService.requestTransaction(id);
        TransactionModel model = mapper.map(entity, TransactionModel.class);
        return ResponseModel.<TransactionModel>builder().data(model).build();
    }
}
