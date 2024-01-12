package com.capstone.workspace.interceptors;

import com.capstone.workspace.enums.auth.AuthErrorCode;
import com.capstone.workspace.exceptions.AppDefinedException;
import com.capstone.workspace.helpers.auth.AuthHelper;
import com.capstone.workspace.models.auth.UserIdentity;
import com.capstone.workspace.services.auth.AuthContextService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class AuthUserInterceptor implements HandlerInterceptor {
    @NonNull
    private AuthContextService authContextService;

    @NonNull
    private AuthHelper authHelper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String authorization = request.getHeader("Authorization");

        if (authorization != null) {
            authContextService.setToken(authorization);

            UserIdentity userIdentity = authHelper.parseUserIdentity(authorization);
            authContextService.setUserIdentity(userIdentity);

            return true;
        }

        throw AppDefinedException.builder().errorCode(AuthErrorCode.MISSING_TOKEN).build();
    }
}
