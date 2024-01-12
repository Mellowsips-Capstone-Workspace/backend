package com.capstone.workspace.dtos.product;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class CreateProductDto {
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
    private List<CreateProductOptionSectionDto> productOptionSections;

    private String storeId;

    private String menuId;

    private String parentId;
}
