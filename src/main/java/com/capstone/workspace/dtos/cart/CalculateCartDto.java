package com.capstone.workspace.dtos.cart;

import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import java.util.Set;

@Setter(AccessLevel.NONE)
@Data
public class CalculateCartDto {
    @Size(min = 1, max = 2)
    private Set<String> vouchers;
}
