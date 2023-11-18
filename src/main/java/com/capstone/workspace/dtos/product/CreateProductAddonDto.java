package com.capstone.workspace.dtos.product;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class CreateProductAddonDto {
    private String name;

    @Min(0)
    private Long price;

    private Boolean isSoldOut;
}
