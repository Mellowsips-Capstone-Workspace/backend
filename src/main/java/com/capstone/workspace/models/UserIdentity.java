package com.capstone.workspace.models;

import com.capstone.workspace.enums.UserType;
import lombok.Data;

@Data
public class UserIdentity {
    private String username;
    private String tenantId;
    private String merchantId;
    private UserType userType;
}
