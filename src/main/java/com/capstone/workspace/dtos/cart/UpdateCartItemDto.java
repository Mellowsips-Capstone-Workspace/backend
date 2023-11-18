package com.capstone.workspace.dtos.cart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
public class UpdateCartItemDto {
    @NotNull
    @Min(1)
    private int quantity;

    private String note;

    private Set<UUID> addons;
}
