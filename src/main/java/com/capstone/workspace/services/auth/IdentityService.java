package com.capstone.workspace.services.auth;

import com.capstone.workspace.helpers.shared.BeanHelper;
import com.capstone.workspace.models.auth.UserIdentity;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Service
@Setter
public class IdentityService {
    private String token;
    private UserIdentity userIdentity;

    public String getToken() {
        try {
            AuthContextService authContextService = BeanHelper.getBean(AuthContextService.class);
            return authContextService.getToken();
        } catch (Exception ex) {
            return token;
        }
    }

    public UserIdentity getUserIdentity() {
        try {
            AuthContextService authContextService = BeanHelper.getBean(AuthContextService.class);
            return authContextService.getUserIdentity();
        } catch (Exception ex) {
            return userIdentity;
        }
    }
}
