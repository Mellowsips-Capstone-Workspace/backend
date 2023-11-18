package com.capstone.workspace.dtos.order;

import com.capstone.workspace.enums.order.TransactionMethod;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateOrderDto {
    @NotNull
    private UUID cartId;

    private UUID qrId;

    private String qrCode;

    @NotNull
    private TransactionMethod initialTransactionMethod;
}
