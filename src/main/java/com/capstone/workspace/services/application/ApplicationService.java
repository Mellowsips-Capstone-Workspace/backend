package com.capstone.workspace.services.application;

import com.capstone.workspace.dtos.application.CreateApplicationDto;
import com.capstone.workspace.dtos.application.SearchApplicationCriteriaDto;
import com.capstone.workspace.dtos.application.SearchApplicationDto;
import com.capstone.workspace.entities.application.Application;
import com.capstone.workspace.enums.application.ApplicationErrorCode;
import com.capstone.workspace.enums.application.ApplicationEvent;
import com.capstone.workspace.enums.application.ApplicationStatus;
import com.capstone.workspace.enums.application.ApplicationType;
import com.capstone.workspace.exceptions.AppDefinedException;
import com.capstone.workspace.exceptions.ConflictException;
import com.capstone.workspace.exceptions.NotFoundException;
import com.capstone.workspace.helpers.application.ApplicationHelper;
import com.capstone.workspace.helpers.shared.AppHelper;
import com.capstone.workspace.models.application.ApplicationModel;
import com.capstone.workspace.models.auth.UserIdentity;
import com.capstone.workspace.models.shared.PaginationResponseModel;
import com.capstone.workspace.repositories.application.ApplicationRepository;
import com.capstone.workspace.services.application.application_machine.ApplicationStateMachine;
import com.capstone.workspace.services.auth.IdentityService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
    private final ApplicationHelper applicationHelper;

    @NonNull
    private final ApplicationStateMachine applicationStateMachine;

    private Logger logger = LoggerFactory.getLogger(ApplicationService.class);

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

        return repository.save(entity);
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

    public Application transition(UUID id, String event) {
        Application entity = getApplicationById(id);
        ApplicationEvent applicationEvent = ApplicationEvent.valueOf(event.toUpperCase());

        if (applicationEvent == ApplicationEvent.SUBMIT || applicationEvent == ApplicationEvent.APPROVE) {
            applicationHelper.validate(entity);
        }

        applicationStateMachine.init(id);

        ApplicationStatus newStatus = applicationStateMachine.transition(entity.getStatus(), applicationEvent);
        if (newStatus != ApplicationStatus.APPROVED) {
            entity.setStatus(newStatus);
        }

        return repository.save(entity);
    }

    public PaginationResponseModel search(SearchApplicationDto dto) {
        String[] searchableFields = new String[]{};
        Map<String, String> filterParams = new HashMap<>();

        SearchApplicationCriteriaDto criteria = dto.getCriteria();
        if (criteria != null) {
            if (criteria.getFilter() != null) {
                BeanUtils.copyProperties(criteria.getFilter(), filterParams);
            }
        }

        String keyword = criteria != null ? criteria.getKeyword() : null;
        Map orderCriteria = criteria != null ? criteria.getOrder() : null;

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
}
