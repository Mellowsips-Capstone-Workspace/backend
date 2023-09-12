package com.capstone.workspace.listeners;

import com.capstone.workspace.entities.shared.BaseEntity;
import com.capstone.workspace.helpers.shared.BeanHelper;
import com.capstone.workspace.models.auth.UserIdentity;
import com.capstone.workspace.services.auth.AuthContextService;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class BaseListener<E extends BaseEntity> {
    @PrePersist
    public void prePersist(E entity) {
        LocalDateTime now = LocalDateTime.now();
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);

        AuthContextService authContextService = BeanHelper.getBean(AuthContextService.class);
        UserIdentity userIdentity = authContextService.getUserIdentity();
        if (userIdentity != null) {
            entity.setCreatedBy(userIdentity.getUsername());
            entity.setUpdatedBy(userIdentity.getUsername());
        }
    }

    @PreUpdate
    public void preUpdate(E entity) {
        entity.setUpdatedAt(LocalDateTime.now());

        AuthContextService authContextService = BeanHelper.getBean(AuthContextService.class);
        UserIdentity userIdentity = authContextService.getUserIdentity();
        if (userIdentity != null) {
            entity.setUpdatedBy(userIdentity.getUsername());
        }
    }
}
