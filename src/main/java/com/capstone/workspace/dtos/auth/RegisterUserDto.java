package com.capstone.workspace.dtos.auth;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.Getter;

@Data
public class RegisterUserDto {
    @NotBlank
    @NotNull
    @Size(max = 30, min = 6)
    private String username;

    @Getter
    @NotBlank
    @NotNull
    @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#]).{8,}$")
    private String password;

    @Getter
    @NotBlank
    @NotNull
    private String displayName;

    @Pattern(regexp = "(84[3|5|7|8|9])+(\\d{8})\\b")
    private String phone;

    @Getter
    @Email
    private String email;
}
