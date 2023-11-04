package com.capstone.workspace.dtos.product;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import java.util.List;

@Setter(AccessLevel.NONE)
@Data
public class CreateProductOptionSectionDto {
    private String name;
    @Min(0)
    private int priority;
    @Min(0)
    private Integer maxAllowedChoices;
    private Boolean isRequired;
    @Valid
    private List<CreateProductAddonDto> productAddons;
}
