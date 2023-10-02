package com.capstone.workspace.dtos.application;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import java.util.Map;

@Setter(AccessLevel.NONE)
@Data
public class UpdateApplicationDto {
    @NotNull
    private Map<String, Object> jsonData;
}
