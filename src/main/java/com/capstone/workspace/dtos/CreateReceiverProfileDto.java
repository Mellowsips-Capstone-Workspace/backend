package com.capstone.workspace.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Builder
@Data
public class CreateReceiverProfileDto {
    @NotBlank
    private String name;

    @NotBlank
    private String phone;

    private Boolean isDefault;
}
