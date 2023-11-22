package com.capstone.workspace.services.notification;

import com.capstone.workspace.dtos.notification.PushNotificationDto;
import com.capstone.workspace.dtos.notification.SearchNotificationDto;
import com.capstone.workspace.dtos.store.SearchStoreCriteriaDto;
import com.capstone.workspace.entities.notification.Notification;
import com.capstone.workspace.enums.user.UserType;
import com.capstone.workspace.exceptions.BadRequestException;
import com.capstone.workspace.exceptions.ConflictException;
import com.capstone.workspace.exceptions.ForbiddenException;
import com.capstone.workspace.exceptions.NotFoundException;
import com.capstone.workspace.helpers.shared.AppHelper;
import com.capstone.workspace.models.auth.UserIdentity;
import com.capstone.workspace.models.notification.NotificationModel;
import com.capstone.workspace.models.shared.PaginationResponseModel;
import com.capstone.workspace.models.store.StoreModel;
import com.capstone.workspace.repositories.notification.NotificationRepository;
import com.capstone.workspace.services.auth.IdentityService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private static Logger logger = LoggerFactory.getLogger(NotificationService.class);

    @NonNull
    private final NotificationRepository repository;

    @NonNull
    private final IdentityService identityService;

    @NonNull
    private final SimpMessagingTemplate simpMessagingTemplate;

    @NonNull
    private final ModelMapper mapper;

    public void createPrivateNotification(PushNotificationDto dto) {
        if (dto.getReceivers() == null || dto.getReceivers().isEmpty()) {
            throw new BadRequestException("This notification has no recipients");
        }

        List<String> receivers = (List<String>) AppHelper.removeDuplicates(dto.getReceivers());

        List<Notification> entities = receivers.stream().map(receiver -> {
            Notification entity = upsert(null, dto);
            entity.setReceiver(receiver);
            return entity;
        }).toList();

        repository.saveAll(entities);

        entities.forEach(entity -> simpMessagingTemplate.convertAndSendToUser(entity.getReceiver(), "/topic/notifications", mapper.map(entity, NotificationModel.class)));
    }

    private Notification upsert(UUID id, Object dto) {
        if (id != null) {
            Notification entity = getOneById(id);
            BeanUtils.copyProperties(dto, entity, AppHelper.commonProperties);
            return entity;
        }

        Notification entity = new Notification();
        BeanUtils.copyProperties(dto, entity, "receivers");
        return entity;
    }

    public Notification getOneById(UUID id) {
        UserIdentity userIdentity = identityService.getUserIdentity();
        String username = userIdentity.getUsername();

        Notification entity = repository.findById(id).orElse(null);

        if (entity == null || !entity.getReceiver().equals(username)) {
            throw new NotFoundException("Notification not found");
        }

        return entity;
    }

    public PaginationResponseModel<NotificationModel> search(SearchNotificationDto dto) {
        String[] searchableFields = new String[]{};

        UserIdentity userIdentity = identityService.getUserIdentity();
        Map<String, Object> filterParams = new HashMap<>(){{
            put("receiver", userIdentity.getUsername());
        }};

        String keyword = null;
        Map orderCriteria = new HashMap<>(){{
            put("createdAt", "DESC");
        }};

        PaginationResponseModel result = repository.searchBy(
                keyword,
                searchableFields,
                filterParams,
                orderCriteria,
                dto.getPagination()
        );

        List<NotificationModel> notificationModels = mapper.map(
                result.getResults(),
                new TypeToken<List<NotificationModel>>() {}.getType()
        );
        result.setResults(notificationModels);

        return result;
    }

    public Notification markAsRead(UUID id) {
        Notification entity = getOneById(id);

        if (Boolean.TRUE.equals(entity.getIsSeen())) {
            throw new ConflictException("Notification has already been read");
        }
        entity.setIsSeen(true);
        entity.setSeenAt(Instant.now());

        return repository.save(entity);
    }

    public void markAllAsRead() {
        UserIdentity userIdentity = identityService.getUserIdentity();
        repository.markAllAsReadByUsername(userIdentity.getUsername());
    }
}
