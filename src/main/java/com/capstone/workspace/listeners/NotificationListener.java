package com.capstone.workspace.listeners;

import com.capstone.workspace.entities.notification.Notification;
import com.capstone.workspace.enums.user.UserType;
import com.capstone.workspace.exceptions.ForbiddenException;
import com.capstone.workspace.helpers.shared.BeanHelper;
import com.capstone.workspace.models.auth.UserIdentity;
import com.capstone.workspace.services.auth.IdentityService;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class NotificationListener {
    @PrePersist
    public void prePersist(Notification entity) {
        entity.setCreatedAt(Instant.now());
    }

    @PreUpdate
    public void preUpdate(Notification entity) {
        IdentityService identityService = BeanHelper.getBean(IdentityService.class);
        UserIdentity userIdentity = identityService.getUserIdentity();
        if (userIdentity != null) {
            verifyUser(userIdentity, entity);
        }
    }

    private void verifyUser(UserIdentity userIdentity, Notification entity) {
        if (userIdentity.getUserType() != UserType.CUSTOMER || !entity.getReceiver().equals(userIdentity.getUsername())) {
            throw new ForbiddenException("Not allow to modify this data");
        }
    }
}
