package com.capstone.workspace.jobs.handlers;

import com.capstone.workspace.entities.application.Application;
import com.capstone.workspace.helpers.shared.BeanHelper;
import com.capstone.workspace.jobs.requests.ApproveApplicationJobRequest;
import com.capstone.workspace.services.application.ApplicationService;
import com.capstone.workspace.services.application.application_approval.ApproveCreateOrganizationApplication;
import com.capstone.workspace.services.application.application_approval.BaseApproveApplication;
import com.capstone.workspace.services.auth.IdentityService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jobrunr.jobs.annotations.Job;
import org.jobrunr.jobs.lambdas.JobRequestHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ApproveApplicationJobRequestHandler implements JobRequestHandler<ApproveApplicationJobRequest> {
    @NonNull
    private final IdentityService identityService;

    @NonNull
    private final ApplicationService applicationService;

    @Override
    @Job(name = "Approve application job")
    public void run(ApproveApplicationJobRequest request) throws Exception {
        identityService.setUserIdentity(request.getUserIdentity());
        Application application = applicationService.getApplicationById(request.getApplicationId());

        BaseApproveApplication handler = getHandler(application);
        if (handler != null) {
            handler.execute(application);
        }
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
