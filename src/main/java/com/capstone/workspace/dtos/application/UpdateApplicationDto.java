package com.capstone.workspace.dtos.application;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

@Data
public class UpdateApplicationDto {
    @NotNull
    private Map<String, Object> jsonData;
}
