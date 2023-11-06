package com.capstone.workspace.dtos.store;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import java.util.List;

@Setter(AccessLevel.NONE)
@Data
public class CreateMenuSectionDto {
    @NotNull
    @NotBlank
    private String name;

    @Min(1)
    private int priority;

    @NotNull
    @NotEmpty
    private List<String> productIds;
}
