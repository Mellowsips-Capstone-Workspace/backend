package com.capstone.workspace.dtos.cart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Setter(AccessLevel.NONE)
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
