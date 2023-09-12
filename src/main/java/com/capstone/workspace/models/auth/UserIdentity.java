package com.capstone.workspace.models.auth;

import com.capstone.workspace.enums.user.UserType;
import lombok.Data;

@Data
public class UserIdentity {
    private String username;
    private String organizationId;
    private String merchantId;
    private UserType userType;
}
