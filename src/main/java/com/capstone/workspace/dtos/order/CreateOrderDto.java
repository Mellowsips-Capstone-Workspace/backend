package com.capstone.workspace.dtos.order;

import com.capstone.workspace.enums.order.TransactionMethod;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import java.util.UUID;

@Setter(AccessLevel.NONE)
@Data
public class CreateOrderDto {
    @NotNull
    private UUID cartId;

    private UUID qrId;

    private String qrCode;

    @NotNull
    private TransactionMethod initialTransactionMethod;
}
