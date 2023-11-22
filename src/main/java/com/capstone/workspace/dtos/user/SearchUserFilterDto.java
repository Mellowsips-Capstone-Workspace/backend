package com.capstone.workspace.dtos.user;

import com.capstone.workspace.enums.auth.AuthProviderType;
import com.capstone.workspace.enums.user.UserType;
import lombok.Data;

@Data
public class SearchUserFilterDto {
    private Boolean isVerified;

    private UserType type;

    private AuthProviderType provider;
}
