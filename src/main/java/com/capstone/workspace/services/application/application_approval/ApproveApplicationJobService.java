package com.capstone.workspace.services.application.application_approval;

import com.capstone.workspace.entities.application.Application;
import com.capstone.workspace.exceptions.NotFoundException;
import com.capstone.workspace.helpers.shared.BeanHelper;
import com.capstone.workspace.models.shared.JobPayload;
import com.capstone.workspace.repositories.application.ApplicationRepository;
import com.capstone.workspace.services.auth.AuthContextService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jobrunr.jobs.annotations.Job;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ApproveApplicationJobService {
    @NonNull
    private final AuthContextService authContextService;

    @NonNull
    private final ApplicationRepository applicationRepository;

    @Job(name = "Approve application job")
    public void execute(JobPayload payload) {
        authContextService.setUserIdentity(payload.getUserIdentity());

        Map<String, Object> data = (Map<String, Object>) payload.getData();
        UUID applicationId = (UUID) data.get("applicationId");
        Application application = getApplicationById(applicationId);

        BaseApproveApplication handler = getHandler(application);
        if (handler != null) {
            handler.execute(application);
        }
    }

    private Application getApplicationById(UUID id) {
        Application entity = applicationRepository.findById(id).orElse(null);

        if (entity == null) {
            throw new NotFoundException("Application not found");
        }

        return entity;
    }

    private BaseApproveApplication getHandler(Application application) {
        switch (application.getType()) {
            case CREATE_ORGANIZATION:
                return BeanHelper.getBean(ApproveCreateOrganizationApplication.class);
            default:
                return null;
        }
    }
}
