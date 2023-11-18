package com.capstone.workspace.dtos.cart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
public class AddProductToCartDto {
    @NotNull
    private UUID productId;

    @NotNull
    @Min(1)
    private int quantity;

    private String note;

    private Set<UUID> addons;
}
