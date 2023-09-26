package com.capstone.workspace.models.auth;

import com.capstone.workspace.enums.user.UserType;
import lombok.Data;

import java.io.Serializable;

@Data
public class UserIdentity implements Serializable {
    private String username;
    private String partnerId;
    private String storeId;
    private UserType userType;
}
