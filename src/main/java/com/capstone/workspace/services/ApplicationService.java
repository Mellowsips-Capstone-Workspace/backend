package com.capstone.workspace.services;

import com.capstone.workspace.dtos.CreateApplicationDto;
import com.capstone.workspace.entities.Application;
import com.capstone.workspace.enums.ApplicationErrorCode;
import com.capstone.workspace.enums.ApplicationStatus;
import com.capstone.workspace.enums.ApplicationType;
import com.capstone.workspace.exceptions.AppDefinedException;
import com.capstone.workspace.exceptions.ConflictException;
import com.capstone.workspace.exceptions.NotFoundException;
import com.capstone.workspace.helpers.ApplicationHelper;
import com.capstone.workspace.models.UserIdentity;
import com.capstone.workspace.repositories.ApplicationRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
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
            applicationHelper.validate(dto);
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
}
