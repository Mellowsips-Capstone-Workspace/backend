package com.capstone.workspace.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Builder
@Data
public class CreateReceiverProfileDto {
    @NotBlank
    @NotNull
    private String name;

    @NotBlank
    @NotNull
    @Pattern(regexp = "(84[3|5|7|8|9])+(\\d{8})\\b")
    private String phone;

    private Boolean isDefault;
}
