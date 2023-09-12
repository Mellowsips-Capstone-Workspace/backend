package com.capstone.workspace.models.shared;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class BaseModel {
    protected UUID id;

    protected LocalDateTime createdAt;

    protected LocalDateTime updatedAt;

    protected String createdBy;

    protected String updatedBy;
}
