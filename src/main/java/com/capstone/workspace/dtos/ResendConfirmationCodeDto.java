package com.capstone.workspace.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ResendConfirmationCodeDto {
    @NotBlank
    @NotNull
    private String username;
}
