package com.capstone.workspace.models.application;

import com.capstone.workspace.enums.application.ApplicationStatus;
import com.capstone.workspace.enums.application.ApplicationType;
import com.capstone.workspace.models.shared.BaseModel;
import lombok.Data;

import java.time.Instant;
import java.util.Map;

@Data
public class ApplicationModel extends BaseModel {
    private Instant approvedAt;

    private String approvedBy;

    private Map<String, Object> jsonData;

    private ApplicationStatus status;

    private ApplicationType type;

    private String partnerId;
}
