package com.capstone.workspace.services.notification;

import com.capstone.workspace.dtos.notification.PushNotificationDto;
import com.capstone.workspace.entities.notification.Notification;
import com.capstone.workspace.enums.user.UserType;
import com.capstone.workspace.exceptions.ForbiddenException;
import com.capstone.workspace.exceptions.NotFoundException;
import com.capstone.workspace.helpers.shared.AppHelper;
import com.capstone.workspace.models.auth.UserIdentity;
import com.capstone.workspace.repositories.notification.NotificationRepository;
import com.capstone.workspace.services.auth.IdentityService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private static Logger logger = LoggerFactory.getLogger(NotificationService.class);

    @NonNull
    private final NotificationRepository repository;

    @NonNull
    private final ModelMapper mapper;

    @NonNull
    private final IdentityService identityService;

    public Notification create(PushNotificationDto dto) {
        logger.info("Pushing notification...");

        Notification entity = upsert(null, dto);
        return repository.save(entity);
    }

    private Notification upsert(UUID id, Object dto) {
        if (id != null) {
            Notification entity = getOneById(id);
            BeanUtils.copyProperties(dto, entity, AppHelper.commonProperties);
            return entity;
        }

        return mapper.map(dto, Notification.class);
    }

    public Notification getOneById(UUID id) {
        UserIdentity userIdentity = identityService.getUserIdentity();
        String username = userIdentity.getUsername();
        UserType userType = userIdentity.getUserType();

        Notification entity = repository.findById(id).orElse(null);

        if (userType != UserType.CUSTOMER) {
            throw new ForbiddenException("Your user type is not allowed to perform this action");
        }

        if (entity == null || !entity.getReceiver().equals(username)) {
            throw new NotFoundException("Notification not found");
        }

        return entity;
    }
}
