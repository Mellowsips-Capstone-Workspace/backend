package com.capstone.workspace.interceptors;

import com.capstone.workspace.enums.UserType;
import com.capstone.workspace.exceptions.UnauthorizedException;
import com.capstone.workspace.helpers.AuthHelper;
import com.capstone.workspace.models.UserIdentity;
import com.capstone.workspace.services.AuthContextService;
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

            if (userIdentity.getUserType() != null && userIdentity.getUserType() != UserType.EMPLOYEE) {
                return true;
            }

            //TODO: Implement code to get merchant id of commercial user
            //String token = authHelper.extractTokenFromAuthorization(authorization);
            return true;
        }

        throw new UnauthorizedException("Unauthorized");
    }
}
