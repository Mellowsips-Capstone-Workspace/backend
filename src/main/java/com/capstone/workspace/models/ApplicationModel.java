package com.capstone.workspace.models;

import com.capstone.workspace.enums.ApplicationStatus;
import com.capstone.workspace.enums.ApplicationType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class ApplicationModel extends BaseModel {
    private LocalDateTime approvedAt;

    private String approvedBy;

    private Map<String, Object> jsonData;

    private ApplicationStatus status;

    private ApplicationType type;
}
