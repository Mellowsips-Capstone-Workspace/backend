package com.capstone.workspace.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ResendConfirmationCodeDto {
    @NotBlank
    private String username;
}
