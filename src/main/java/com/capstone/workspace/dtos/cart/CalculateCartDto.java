package com.capstone.workspace.dtos.cart;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class CalculateCartDto {
    @NotNull
    @Size(min = 1, max = 2)
    private Set<String> vouchers;
}
