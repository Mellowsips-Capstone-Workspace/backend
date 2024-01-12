package com.capstone.workspace.dtos.product;

import jakarta.validation.constraints.Min;
import lombok.Data;

import java.util.UUID;

@Data
public class UpdateProductAddonDto {
    private UUID id;

    private String name;

    @Min(0)
    private Long price;

    private Boolean isSoldOut;
}
