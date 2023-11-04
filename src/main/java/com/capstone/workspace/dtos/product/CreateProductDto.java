package com.capstone.workspace.dtos.product;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NonNull;
import lombok.Setter;

import java.util.List;

@Setter(AccessLevel.NONE)
@Data
public class CreateProductDto {
    @NotBlank
    private String name;
    @Min(0)
    private long price;
    private String coverImage;
    private String description;
    private List<String> categories;
    private String storeId;
    @Valid
    private List<CreateProductOptionSectionDto> productOptionSections;
}
