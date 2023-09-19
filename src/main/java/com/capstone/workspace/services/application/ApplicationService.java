package com.capstone.workspace.services.application;

import com.capstone.workspace.dtos.application.CreateApplicationDto;
import com.capstone.workspace.entities.application.Application;
import com.capstone.workspace.enums.application.ApplicationErrorCode;
import com.capstone.workspace.enums.application.ApplicationEvent;
import com.capstone.workspace.enums.application.ApplicationStatus;
import com.capstone.workspace.enums.application.ApplicationType;
import com.capstone.workspace.exceptions.AppDefinedException;
import com.capstone.workspace.exceptions.ConflictException;
import com.capstone.workspace.exceptions.ForbiddenException;
import com.capstone.workspace.exceptions.NotFoundException;
import com.capstone.workspace.helpers.application.ApplicationHelper;
import com.capstone.workspace.models.auth.UserIdentity;
import com.capstone.workspace.repositories.application.ApplicationRepository;
import com.capstone.workspace.services.application.application_machine.ApplicationStateMachine;
import com.capstone.workspace.services.auth.AuthContextService;
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
public class ApplicationService {
    @NonNull
    private final ApplicationRepository repository;

    @NonNull
    private final AuthContextService authContextService;

    @NonNull
    private final ModelMapper mapper;

    @NonNull
    private final ApplicationHelper applicationHelper;

    @NonNull
    private final ApplicationStateMachine applicationStateMachine;

    private Logger logger = LoggerFactory.getLogger(ApplicationService.class);

    public Application create(CreateApplicationDto dto) {
        Application entity = upsert(null, dto);

        if (dto.getStatus() != ApplicationStatus.DRAFT && dto.getStatus() != ApplicationStatus.WAITING_FOR_APPROVAL) {
            throw AppDefinedException.builder().errorCode(ApplicationErrorCode.STATUS_NOT_ALLOWED).build();
        }

        UserIdentity userIdentity = authContextService.getUserIdentity();
        if (dto.getType() == ApplicationType.CREATE_ORGANIZATION) {
            if (userIdentity.getOrganizationId() != null) {
                throw AppDefinedException.builder().errorCode(ApplicationErrorCode.ORGANIZATION_ALREADY_EXIST).build();
            }

            Application application = repository.findByCreatedByAndStatusIsNotAndTypeIs(
                userIdentity.getUsername(),
                ApplicationStatus.REJECTED,
                ApplicationType.CREATE_ORGANIZATION
            );
            if (application != null) {
                throw new ConflictException("Your application for creating organization already exists");
            }
        } else if (userIdentity.getOrganizationId() == null) {
            throw AppDefinedException.builder().errorCode(ApplicationErrorCode.ORGANIZATION_NOT_EXIST_YET).build();
        }

        if (dto.getStatus() == ApplicationStatus.WAITING_FOR_APPROVAL) {
            applicationHelper.validate(mapper.map(dto, Application.class));
        }

        return repository.save(entity);
    }

    private Application upsert(UUID id, Object dto) {
        if (id != null) {
            Application entity = getApplicationById(id);
            BeanUtils.copyProperties(dto, entity);
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

    public Application transition(UUID id, String event) {
        Application entity = getApplicationById(id);
        ApplicationEvent applicationEvent = ApplicationEvent.valueOf(event.toUpperCase());

        if (applicationEvent == ApplicationEvent.SUBMIT || applicationEvent == ApplicationEvent.APPROVE) {
            applicationHelper.validate(entity);
        }

        applicationStateMachine.init(id);

        ApplicationStatus newStatus = applicationStateMachine.transition(entity.getStatus(), applicationEvent);
        logger.info(String.valueOf(newStatus));
        logger.info(String.valueOf(applicationEvent));
        if (newStatus != ApplicationStatus.APPROVED) {
            entity.setStatus(newStatus);
        }

        return repository.save(entity);
    }
}
