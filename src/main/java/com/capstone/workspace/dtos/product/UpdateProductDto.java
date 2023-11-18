package com.capstone.workspace.dtos.product;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import java.util.List;

@Setter(AccessLevel.NONE)
@Data
public class UpdateProductDto {
    @NotNull
    @NotBlank
    private String name;

    @Min(0)
    private long price;

    @NotNull
    @NotBlank
    private String coverImage;

    private String description;

    private List<String> categories;

    @Valid
    private List<UpdateProductOptionSectionDto> productOptionSections;
}
