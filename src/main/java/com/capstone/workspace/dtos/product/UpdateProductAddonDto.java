package com.capstone.workspace.dtos.product;

import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import java.util.UUID;

@Setter(AccessLevel.NONE)
@Data
public class UpdateProductAddonDto {
    private UUID id;
    private String name;
    @Min(0)
    private Long price;
    private Boolean isSoldOut;
}
