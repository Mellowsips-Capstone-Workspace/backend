package com.capstone.workspace.listeners;

import com.capstone.workspace.entities.partner.IPartnerEntity;
import com.capstone.workspace.entities.shared.BaseEntity;
import com.capstone.workspace.entities.store.IStoreEntity;
import com.capstone.workspace.enums.user.UserType;
import com.capstone.workspace.helpers.shared.BeanHelper;
import com.capstone.workspace.models.auth.UserIdentity;
import com.capstone.workspace.services.auth.IdentityService;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class BaseListener<E extends BaseEntity> {
    @PrePersist
    public void prePersist(E entity) {
        Instant now = Instant.now();
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);

        IdentityService identityService = BeanHelper.getBean(IdentityService.class);
        UserIdentity userIdentity = identityService.getUserIdentity();
        if (userIdentity != null) {
            entity.setCreatedBy(userIdentity.getUsername());
            entity.setUpdatedBy(userIdentity.getUsername());

            if (userIdentity.getUserType() == UserType.EMPLOYEE) {
                if (entity instanceof IPartnerEntity) {
                    ((IPartnerEntity) entity).setPartnerId(userIdentity.getPartnerId());
                }

                if (entity instanceof IStoreEntity && userIdentity.getStoreId() != null) {
                    ((IStoreEntity) entity).setStoreId(userIdentity.getStoreId());
                }
            }
        }
    }

    @PreUpdate
    public void preUpdate(E entity) {
        entity.setUpdatedAt(Instant.now());

        IdentityService identityService = BeanHelper.getBean(IdentityService.class);
        UserIdentity userIdentity = identityService.getUserIdentity();
        if (userIdentity != null) {
            entity.setUpdatedBy(userIdentity.getUsername());
        }
    }
}
