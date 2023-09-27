package com.capstone.workspace.dtos.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class CreateRoleDto {
    @NotBlank
    @NotNull
    private String name;

    private String description;

    @NotEmpty
    @NotNull
    private List<String> permissions;

    @Builder.Default
    private Boolean isAllowedEdit = true;
}
