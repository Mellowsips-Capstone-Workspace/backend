package com.capstone.workspace.dtos.store;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import java.util.List;

@Setter(AccessLevel.NONE)
@Data
public class CreateMenuDto {
    @NotNull
    @NotBlank
    private String name;

    private Boolean isActive;

    @Valid
    @NotNull
    @NotEmpty
    private List<CreateMenuSectionDto> menuSections;

    private String storeId;
}
