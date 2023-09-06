package com.capstone.workspace.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Setter(AccessLevel.NONE)
@Data
public class VerifyUserDto {
    @NotBlank
    private String username;

    @NotBlank
    private String confirmationCode;
}
