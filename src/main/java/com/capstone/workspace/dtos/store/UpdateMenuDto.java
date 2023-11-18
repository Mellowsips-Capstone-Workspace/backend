package com.capstone.workspace.dtos.store;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class UpdateMenuDto {
    @NotNull
    @NotBlank
    private String name;

    private Boolean isActive;

    @Valid
    @NotNull
    @NotEmpty
    private List<UpdateMenuSectionDto> menuSections;
}
