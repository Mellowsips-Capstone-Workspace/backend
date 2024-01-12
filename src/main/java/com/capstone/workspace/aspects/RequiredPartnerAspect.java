package com.capstone.workspace.aspects;

import com.capstone.workspace.annotations.RequiredPartner;
import com.capstone.workspace.exceptions.ForbiddenException;
import com.capstone.workspace.models.auth.UserIdentity;
import com.capstone.workspace.services.auth.IdentityService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class RequiredPartnerAspect {
    @NonNull
    private IdentityService identityService;

    @Before("@annotation(requiredPartner)")
    public void checkUserType(RequiredPartner requiredPartner) {
        UserIdentity userIdentity = identityService.getUserIdentity();
        if (userIdentity.getPartnerId() == null) {
            throw new ForbiddenException("You must be a MellowSips partner");
        }
    }
}
