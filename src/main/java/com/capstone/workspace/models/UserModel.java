package com.capstone.workspace.models;

import com.capstone.workspace.enums.AuthProviderType;
import com.capstone.workspace.enums.UserType;
import lombok.Data;

@Data
public class UserModel extends BaseModel {
    private String username;

    private String displayName;

    private String phone;

    private String email;

    private Boolean isVerified;

    private String avatar;

    private UserType type;

    private AuthProviderType provider;
}
