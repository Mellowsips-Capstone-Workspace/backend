package com.capstone.workspace.dtos.application;

import com.capstone.workspace.enums.application.ApplicationStatus;
import com.capstone.workspace.enums.application.ApplicationType;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import java.util.Map;

@Setter(AccessLevel.NONE)
@Data
public class CreateApplicationDto {
    @NotNull
    private Map<String, Object> jsonData;

    @NotNull
    private ApplicationStatus status;

    @NotNull
    private ApplicationType type;
}
