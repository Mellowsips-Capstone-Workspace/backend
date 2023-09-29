package com.capstone.workspace.dtos.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Setter(AccessLevel.NONE)
@Data
public class ResetPasswordDto {
    @NotBlank
    @NotNull
    private String username;

    @NotBlank
    @NotNull
    private String password;

    @NotBlank
    @NotNull
    private String newPassword;
}
