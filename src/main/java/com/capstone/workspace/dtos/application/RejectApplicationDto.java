package com.capstone.workspace.dtos.application;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RejectApplicationDto {
    @NotNull
    @NotBlank
    private String reason;
}
