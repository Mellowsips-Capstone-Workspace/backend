package com.capstone.workspace.models.user;

import com.capstone.workspace.enums.auth.AuthProviderType;
import com.capstone.workspace.enums.user.UserType;
import com.capstone.workspace.models.shared.BaseModel;
import lombok.Data;

@Data
public class UserModel extends BaseModel {
    private String username;

    private String displayName;

    private String phone;

    private String email;

    private Boolean isVerified;

    private Boolean isActive;

    private String avatar;

    private UserType type;

    private AuthProviderType provider;

    private String partnerId;

    private String storeId;
}
