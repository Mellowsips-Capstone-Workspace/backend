package com.capstone.workspace.listeners;

import com.capstone.workspace.entities.partner.IPartnerEntity;
import com.capstone.workspace.entities.shared.BaseEntity;
import com.capstone.workspace.entities.store.IStoreEntity;
import com.capstone.workspace.entities.store.Store;
import com.capstone.workspace.entities.user.User;
import com.capstone.workspace.enums.user.UserType;
import com.capstone.workspace.exceptions.ForbiddenException;
import com.capstone.workspace.helpers.shared.BeanHelper;
import com.capstone.workspace.models.auth.UserIdentity;
import com.capstone.workspace.services.auth.IdentityService;
import com.capstone.workspace.services.store.StoreService;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

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

                if (entity instanceof IStoreEntity) {
                    if (userIdentity.getStoreId() != null) {
                        ((IStoreEntity) entity).setStoreId(userIdentity.getStoreId());
                    }

                    String storeId = ((IStoreEntity) entity).getStoreId();
                    StoreService storeService = BeanHelper.getBean(StoreService.class);
                    Store store = storeService.getStoreById(UUID.fromString(storeId));
                    if (!store.getPartnerId().equals(userIdentity.getPartnerId())) {
                        throw new ForbiddenException("Not allow to create this data");
                    }
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
            verifyUser(userIdentity, entity);
            entity.setUpdatedBy(userIdentity.getUsername());
        }
    }

    private void verifyUser(UserIdentity userIdentity, E entity) {
        if (userIdentity.getUserType() != UserType.EMPLOYEE) {
            return;
        }

        if (userIdentity.getPartnerId() == null) {
            if (
                !entity.getCreatedBy().equals(userIdentity.getUsername())
                || (entity instanceof User && !((User) entity).getUsername().equals(userIdentity.getUsername()))
            ) {
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
