package com.capstone.workspace.dtos.product;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class UpdateProductOptionSectionDto {
    private UUID id;

    @NotNull
    @NotBlank
    private String name;

    @NotNull
    @Min(0)
    private int priority;

    @Min(1)
    private Integer maxAllowedChoices;

    private Boolean isRequired;

    @Valid
    @Size(min = 1)
    @NotNull
    private List<UpdateProductAddonDto> productAddons;
}
