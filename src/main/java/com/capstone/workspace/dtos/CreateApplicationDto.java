package com.capstone.workspace.dtos;

import com.capstone.workspace.enums.ApplicationStatus;
import com.capstone.workspace.enums.ApplicationType;
import jakarta.validation.constraints.NotBlank;
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
