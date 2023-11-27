package com.capstone.workspace.services.application;

import com.capstone.workspace.dtos.application.*;
import com.capstone.workspace.dtos.document.UpdateDocumentDto;
import com.capstone.workspace.dtos.notification.PushNotificationDto;
import com.capstone.workspace.entities.application.Application;
import com.capstone.workspace.entities.user.User;
import com.capstone.workspace.enums.application.ApplicationErrorCode;
import com.capstone.workspace.enums.application.ApplicationEvent;
import com.capstone.workspace.enums.application.ApplicationStatus;
import com.capstone.workspace.enums.application.ApplicationType;
import com.capstone.workspace.enums.notification.NotificationKey;
import com.capstone.workspace.enums.user.UserType;
import com.capstone.workspace.exceptions.*;
import com.capstone.workspace.helpers.application.ApplicationHelper;
import com.capstone.workspace.helpers.shared.AppHelper;
import com.capstone.workspace.models.application.ApplicationModel;
import com.capstone.workspace.models.auth.UserIdentity;
import com.capstone.workspace.models.shared.PaginationResponseModel;
import com.capstone.workspace.repositories.application.ApplicationRepository;
import com.capstone.workspace.repositories.user.UserRepository;
import com.capstone.workspace.services.application.application_machine.ApplicationStateMachine;
import com.capstone.workspace.services.auth.IdentityService;
import com.capstone.workspace.services.document.DocumentService;
import com.capstone.workspace.services.shared.JobService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ApplicationService {
    @NonNull
    private final ApplicationRepository repository;

    @NonNull
    private final IdentityService identityService;

    @NonNull
    private final ModelMapper mapper;

    @NonNull
    private final ObjectMapper objectMapper;

    @NonNull
    private final ApplicationHelper applicationHelper;

    @NonNull
    private final ApplicationStateMachine applicationStateMachine;

    @NonNull
    private final DocumentService documentService;

    @NonNull
    private final JobService jobService;

    @NonNull
    private final UserRepository userRepository;

    @Transactional
    public Application create(CreateApplicationDto dto) {
        Application entity = upsert(null, dto);

        if (dto.getStatus() != ApplicationStatus.DRAFT && dto.getStatus() != ApplicationStatus.WAITING_FOR_APPROVAL) {
            throw AppDefinedException.builder().errorCode(ApplicationErrorCode.STATUS_NOT_ALLOWED).build();
        }

        UserIdentity userIdentity = identityService.getUserIdentity();
        if (dto.getType() == ApplicationType.CREATE_ORGANIZATION) {
            if (userIdentity.getPartnerId() != null) {
                throw AppDefinedException.builder().errorCode(ApplicationErrorCode.PARTNER_ALREADY_EXIST).build();
            }

            Application application = repository.findByCreatedByAndStatusIsNotAndTypeIs(
                userIdentity.getUsername(),
                ApplicationStatus.REJECTED,
                ApplicationType.CREATE_ORGANIZATION
            );
            if (application != null) {
                throw new ConflictException("Your application for creating organization already exists");
            }
        } else if (userIdentity.getPartnerId() == null) {
            throw AppDefinedException.builder().errorCode(ApplicationErrorCode.PARTNER_NOT_EXIST_YET).build();
        }

        if (dto.getStatus() == ApplicationStatus.WAITING_FOR_APPROVAL) {
            applicationHelper.validate(mapper.map(dto, Application.class));
        }

        Application saved = repository.save(entity);
        pushNotificationForAdmins(saved);
        updateApplicationDocument(saved);

        return saved;
    }

    private void pushNotificationForAdmins(Application entity) {
        List<User> admins = userRepository.findByType(UserType.ADMIN);
        List<String> adminUsernames = admins.stream().map(User::getUsername).toList();

        PushNotificationDto pushNotificationDto = PushNotificationDto.builder()
                .key(String.valueOf(NotificationKey.HAVING_NEW_APPLICATION))
                .subject("Bạn có yêu cầu mới từ doanh nghiệp")
                .content("Có yêu cầu tạo mới doanh nghiệp cần bạn xử lí.")
                .receivers(adminUsernames)
                .metadata(new HashMap<>(){{
                    put("applicationId", entity.getId());
                }})
                .build();

        jobService.publishPushNotificationJob(pushNotificationDto);
    }

    private Application upsert(UUID id, Object dto) {
        if (id != null) {
            Application entity = getApplicationById(id);
            BeanUtils.copyProperties(dto, entity, AppHelper.commonProperties);
            return entity;
        }

        return mapper.map(dto, Application.class);
    }

    public Application getApplicationById(UUID id) {
        Application entity = repository.findById(id).orElse(null);

        if (entity == null) {
            throw new NotFoundException("Application not found");
        }

        return entity;
    }

    public synchronized Application transition(UUID id, String event, RejectApplicationDto dto) {
        Application entity = getApplicationById(id);
        ApplicationEvent applicationEvent = ApplicationEvent.valueOf(event.toUpperCase());

        if (applicationEvent == ApplicationEvent.SUBMIT || applicationEvent == ApplicationEvent.APPROVE) {
            applicationHelper.validate(entity);
        }

        applicationStateMachine.init(id);

        ApplicationStatus newStatus = applicationStateMachine.transition(entity.getStatus(), applicationEvent);
        if (newStatus != ApplicationStatus.APPROVED) {
            entity.setStatus(newStatus);

            PushNotificationDto pushNotificationDto = null;

            switch (newStatus) {
                case REJECTED:
                    entity.setRejectReason(dto.getReason());

                    pushNotificationDto = PushNotificationDto.builder()
                        .key(String.valueOf(NotificationKey.APPLICATION_REJECTED))
                        .subject("Yêu cầu của bạn đã bị từ chối")
                        .content("Lí do: " + dto.getReason())
                        .receivers(List.of(entity.getCreatedBy()))
                        .metadata(new HashMap<>(){{
                            put("applicationId", entity.getId());
                        }})
                        .build();

                    break;
                case PROCESSING:
                    pushNotificationDto = PushNotificationDto.builder()
                        .key(String.valueOf(NotificationKey.APPLICATION_PROCESSING))
                        .subject("Yêu cầu của bạn đang được xử lí")
                        .content("Hệ thống đang bắt đầu xử lí yêu cầu tạo/thay đổi doanh nghiệp của bạn")
                        .receivers(List.of(entity.getCreatedBy()))
                        .metadata(new HashMap<>(){{
                            put("applicationId", entity.getId());
                        }})
                        .build();
                    break;
                case WAITING_FOR_APPROVAL:
                    List<User> admins = userRepository.findByType(UserType.ADMIN);
                    List<String> adminUsernames = admins.stream().map(User::getUsername).toList();

                    pushNotificationDto = PushNotificationDto.builder()
                        .key(String.valueOf(NotificationKey.APPLICATION_PROCESSING))
                        .subject("Hệ thống có đơn yêu cầu mới")
                        .content("Bạn có đơn yêu cầu mới cần xử lí")
                        .receivers(adminUsernames)
                        .metadata(new HashMap<>(){{
                            put("applicationId", entity.getId());
                        }})
                        .build();
                    break;
                default:
                    break;
            }

            if (pushNotificationDto != null) {
                jobService.publishPushNotificationJob(pushNotificationDto);
            }
        }

        return repository.save(entity);
    }

    public PaginationResponseModel search(SearchApplicationDto dto) {
        String[] searchableFields = new String[]{};
        Map<String, Object> filterParams = Collections.emptyMap();

        SearchApplicationCriteriaDto criteria = dto.getCriteria();
        String keyword = null;
        Map orderCriteria = null;

        if (criteria != null) {
            if (criteria.getFilter() != null) {
                filterParams = AppHelper.copyPropertiesToMap(criteria.getFilter());
            }
            keyword = criteria.getKeyword();
            orderCriteria = criteria.getOrder();
        }

        PaginationResponseModel result = repository.searchBy(
            keyword,
            searchableFields,
            filterParams,
            orderCriteria,
            dto.getPagination()
        );

        List<ApplicationModel> applicationModels = mapper.map(
            result.getResults(),
            new TypeToken<List<ApplicationModel>>() {}.getType()
        );
        result.setResults(applicationModels);

        return result;
    }

    private void updateApplicationDocument(Application application) {
        try {
            List<String> documentIds = new ArrayList<>();
            Map<String, List<String>> documentFields = getApplicationDocumentFields();

            Map<String, Object> data = application.getJsonData();
            JsonNode jsonData = objectMapper.readTree(objectMapper.writeValueAsString(data));

            List<String> nodeKeys = List.of("organization", "bankAccount", "merchant");
            for (String key: nodeKeys) {
                JsonNode node = jsonData.get(key);
                List<String> fields = documentFields.get(key);

                if (node.isArray()) {
                    for (JsonNode n: node) {
                        findDocumentIds(documentIds, fields, n);
                    }
                } else {
                    findDocumentIds(documentIds, fields, node);
                }
            }

            UpdateDocumentDto dto = UpdateDocumentDto.builder()
                    .reference(application.getId())
                    .referenceType("application")
                    .build();

            documentIds.forEach(item -> {
                int lastIndex = item.lastIndexOf("|");
                String documentId = item.substring(lastIndex + 1);
                documentService.updateDocument(UUID.fromString(documentId), dto);
            });
        } catch (JsonProcessingException ex) {
            throw new InternalServerErrorException();
        }
    }

    private Map<String, List<String>> getApplicationDocumentFields() {
        Map<String, List<String>> documentFields = new HashMap<>();

        documentFields.put("organization", List.of("identityFrontImage", "identityBackImage", "businessIdentityImages"));
        documentFields.put("bankAccount", List.of("identityImages"));
        documentFields.put("merchant", List.of("menuImages", "merchantImages"));

        return documentFields;
    }

    private List<String> findDocumentIds(List<String> documentIds, List<String> fields, JsonNode node) {
        fields.forEach(field -> {
            JsonNode value = node.get(field);

            if (value != null && !value.isEmpty()) {
                if (value.isTextual()) documentIds.add(value.asText());
                else if (value.isArray()) {
                    for (JsonNode item: value) {
                        documentIds.add(item.asText());
                    }
                }
            }
        });

        return documentIds;
    }

    public Application update(UUID id, UpdateApplicationDto params) {
        Application entity = upsert(id, params);

        if (entity.getStatus() == ApplicationStatus.APPROVED || entity.getStatus() == ApplicationStatus.REJECTED) {
            throw new ForbiddenException("Not allow to update application after close");
        }

        updateApplicationDocument(entity);

        return repository.save(entity);
    }
}
