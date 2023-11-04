package com.capstone.workspace.dtos.store;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import java.util.List;

@Setter(AccessLevel.NONE)
@Data
public class CreateMenuSectionDto {
    @NotBlank
    private String name;

    @Min(1)
    private int priority;

    @NotEmpty
    private List<String> productIds;
}
