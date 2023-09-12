package com.capstone.workspace.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Setter(AccessLevel.NONE)
@Data
public class PasswordLoginDto {
    @NotBlank
    @NotNull
    private String username;

    @NotBlank
    @NotNull
    private String password;
}
