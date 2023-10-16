package com.capstone.workspace.helpers.auth;

import com.capstone.workspace.entities.user.User;
import com.capstone.workspace.enums.auth.AuthErrorCode;
import com.capstone.workspace.exceptions.AppDefinedException;
import com.capstone.workspace.models.auth.UserIdentity;
import com.capstone.workspace.services.auth.JwtService;
import com.capstone.workspace.services.user.UserService;
import io.jsonwebtoken.Claims;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthHelper {
    private static final Logger logger = LoggerFactory.getLogger(AuthHelper.class);

    @NonNull
    private final JwtService jwtService;

    @NonNull
    private final UserService userService;

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
        User user = userService.getUserByUsername(username);

        userIdentity.setUsername(username);
        userIdentity.setUserType(user.getType());
        userIdentity.setStoreId(user.getStoreId());
        userIdentity.setPartnerId(user.getPartnerId());

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
