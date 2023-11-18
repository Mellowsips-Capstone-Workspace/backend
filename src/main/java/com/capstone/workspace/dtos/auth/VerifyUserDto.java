package com.capstone.workspace.dtos.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VerifyUserDto {
    @NotBlank
    @NotNull
    private String username;

    @NotBlank
    @NotNull
    private String confirmationCode;
}
