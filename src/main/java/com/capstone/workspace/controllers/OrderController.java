package com.capstone.workspace.controllers;

import com.capstone.workspace.annotations.AllowedUsers;
import com.capstone.workspace.dtos.order.CreateOrderDto;
import com.capstone.workspace.entities.order.Order;
import com.capstone.workspace.enums.user.UserType;
import com.capstone.workspace.models.order.OrderModel;
import com.capstone.workspace.models.shared.ResponseModel;
import com.capstone.workspace.services.order.OrderService;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping( "/api/orders")
@RequiredArgsConstructor
public class OrderController {
    @NonNull
    private final OrderService orderService;

    @NonNull
    private final ModelMapper mapper;

    @AllowedUsers(userTypes = {UserType.CUSTOMER})
    @PostMapping
    public ResponseModel<OrderModel> createOrder(@Valid @RequestBody CreateOrderDto dto) {
        Order entity = orderService.create(dto);
        OrderModel model = mapper.map(entity, OrderModel.class);
        return ResponseModel.<OrderModel>builder().data(model).build();
    }

    @GetMapping("/{id}")
    public ResponseModel<OrderModel> getOrderById(@PathVariable UUID id) {
        Order entity = orderService.getOneById(id);
        OrderModel model = mapper.map(entity, OrderModel.class);
        return ResponseModel.<OrderModel>builder().data(model).build();
    }
}
