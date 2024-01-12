package com.capstone.workspace.dtos.store;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CreateMenuSectionDto {
    @NotNull
    @NotBlank
    private String name;

    @Min(0)
    private int priority;

    @NotNull
    @NotEmpty
    private List<String> productIds;
}
