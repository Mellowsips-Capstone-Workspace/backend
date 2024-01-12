package com.capstone.workspace.dtos.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UpdateUserProfileDto {
    @NotNull
    @NotBlank
    private String displayName;

    @Pattern(regexp = "(84[3|5|7|8|9])+(\\d{8})\\b")
    private String phone;

    @Email
    private String email;

    private String avatar;
}
