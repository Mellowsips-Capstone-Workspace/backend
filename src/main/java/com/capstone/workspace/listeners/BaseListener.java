package com.capstone.workspace.listeners;

import com.capstone.workspace.entities.partner.IPartnerEntity;
import com.capstone.workspace.entities.shared.BaseEntity;
import com.capstone.workspace.entities.store.IStoreEntity;
import com.capstone.workspace.enums.user.UserType;
import com.capstone.workspace.exceptions.ForbiddenException;
import com.capstone.workspace.exceptions.UnauthorizedException;
import com.capstone.workspace.helpers.shared.BeanHelper;
import com.capstone.workspace.models.auth.UserIdentity;
import com.capstone.workspace.services.auth.IdentityService;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import org.slf4j.LoggerFactory;
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
        verifyUser(userIdentity, entity);
        entity.setUpdatedBy(userIdentity.getUsername());
    }

    private void verifyUser(UserIdentity userIdentity, E entity) {
        if (userIdentity == null) {
            throw new UnauthorizedException("Unauthorized");
        }

        if (userIdentity.getUserType() != UserType.EMPLOYEE) {
            return;
        }

        if (userIdentity.getPartnerId() == null) {
            if (!entity.getCreatedBy().equals(userIdentity.getUsername())) {
                throw new ForbiddenException("Not allow to modify this data");
            }
        } else {
            if (
                entity instanceof IPartnerEntity
                && (((IPartnerEntity) entity).getPartnerId() == null || !((IPartnerEntity) entity).getPartnerId().equals(userIdentity.getPartnerId()))
            ) {
                throw new ForbiddenException("Not allow to modify this data");
            }
        }
    }
}
