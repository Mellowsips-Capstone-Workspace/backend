package com.capstone.workspace.dtos.cart;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class CalculateCartDto {
    @Size(min = 1, max = 2)
    private Set<String> vouchers;
}
