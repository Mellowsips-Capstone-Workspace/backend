package com.capstone.workspace.helpers.auth;

import com.capstone.workspace.enums.auth.AuthErrorCode;
import com.capstone.workspace.enums.user.UserType;
import com.capstone.workspace.exceptions.AppDefinedException;
import com.capstone.workspace.helpers.shared.AppHelper;
import com.capstone.workspace.models.auth.UserIdentity;
import com.capstone.workspace.services.auth.JwtService;
import io.jsonwebtoken.Claims;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class AuthHelper {
    private static final Logger logger = LoggerFactory.getLogger(AuthHelper.class);

    @NonNull
    private JwtService jwtService;

    public UserIdentity parseUserIdentity(String authorization) {
        if (authorization != null) {
            String token = extractTokenFromAuthorization(authorization);

            if (token != null) {
                Claims tokenPayload = this.jwtService.decode(token);
                return decodeUserIdentity(tokenPayload);
            }
        }

        return null;
    }

    public UserIdentity decodeUserIdentity(Claims tokenPayload) {
        UserIdentity userIdentity = new UserIdentity();

        String username = tokenPayload.get("username", String.class);
        userIdentity.setUsername(username);

        ArrayList<String> groups = tokenPayload.get("cognito:groups", ArrayList.class);
        if (groups != null && !groups.isEmpty()) {
            if (groups.get(0).equals("mellowsips_admin")) {
                userIdentity.setUserType(UserType.ADMIN);
            } else {
                userIdentity.setPartnerId(groups.get(0));
            }
        }

        if (userIdentity.getUserType() == null) {
            if (AppHelper.isVietnamNumberPhone(username)) {
                userIdentity.setUserType(UserType.CUSTOMER);
            } else {
                userIdentity.setUserType(UserType.EMPLOYEE);
            }
        }

        return userIdentity;
    }

    public Claims decodeUserToken(String authorization) {
        String token = this.extractTokenFromAuthorization(authorization);
        return this.jwtService.decode(token);
    }

    public String extractTokenFromAuthorization(String authorization) {
        if (authorization == null) {
            throw AppDefinedException.builder().errorCode(AuthErrorCode.MISSING_TOKEN).build();
        }

        if (authorization.startsWith("Bearer")) {
            return authorization.substring("Bearer ".length());
        }

        return authorization;
    }
}
