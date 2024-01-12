package com.capstone.workspace.dtos.order;

import com.capstone.workspace.enums.order.TransactionMethod;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
public class CreateOrderDto {
    @NotNull
    private UUID cartId;

    private UUID qrId;

    private String qrCode;

    @NotNull
    private TransactionMethod initialTransactionMethod;

    @Size(max = 2)
    private Set<String> vouchers;
}
