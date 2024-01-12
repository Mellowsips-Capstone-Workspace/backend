package com.capstone.workspace.services.auth;

import com.capstone.workspace.models.auth.UserIdentity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

@Service
@Getter(AccessLevel.PACKAGE)
@Setter
@RequestScope
public class AuthContextService {
    private String token;
    private UserIdentity userIdentity;
}
