package com.capstone.workspace.dtos.product;

import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Setter(AccessLevel.NONE)
@Data
public class CreateProductAddonDto {
    private String name;
    @Min(0)
    private Long price;
}
