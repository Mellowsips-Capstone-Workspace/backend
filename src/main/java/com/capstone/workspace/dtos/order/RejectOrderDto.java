package com.capstone.workspace.dtos.order;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RejectOrderDto {
    @NotNull
    @NotBlank
    private String reason;
}
