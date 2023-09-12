package com.capstone.workspace.aspects;

import com.capstone.workspace.annotations.AllowedUsers;
import com.capstone.workspace.enums.user.UserType;
import com.capstone.workspace.exceptions.ForbiddenException;
import com.capstone.workspace.exceptions.UnauthorizedException;
import com.capstone.workspace.models.auth.UserIdentity;
import com.capstone.workspace.services.auth.AuthContextService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Aspect
@Component
@RequiredArgsConstructor
public class AllowedUsersAspect {
    private Logger logger = LoggerFactory.getLogger(AllowedUsersAspect.class);

    @NonNull
    private AuthContextService authContextService;

    @Before("@annotation(allowedUsers)")
    public void checkUserType(AllowedUsers allowedUsers) {
        UserIdentity userIdentity = authContextService.getUserIdentity();
        if (userIdentity == null || userIdentity.getUsername() == null) {
            throw new UnauthorizedException("Unauthorized");
        }

        List<UserType> userTypes = Arrays.asList(allowedUsers.userTypes());
        if (!userTypes.contains(userIdentity.getUserType())) {
            throw new ForbiddenException("Your user type is not allowed to perform this action");
        }
    }
}
