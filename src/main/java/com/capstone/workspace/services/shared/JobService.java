package com.capstone.workspace.services.shared;

import com.capstone.workspace.models.shared.JobPayload;
import com.capstone.workspace.services.application.ApplicationService;
import com.capstone.workspace.services.application.application_approval.ApproveApplicationJobService;
import com.capstone.workspace.services.auth.AuthContextService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jobrunr.scheduling.JobScheduler;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class JobService {
    @NonNull
    private final JobScheduler jobScheduler;

    @NonNull
    private final AuthContextService authContextService;

    @NonNull
    private final ApproveApplicationJobService approveApplicationJobService;

    public void publishApprovedApplicationJob(Map<String, Object> payload) {
        jobScheduler.enqueue(() -> approveApplicationJobService.execute(preparePayload(payload)));
    }

    private JobPayload preparePayload(Object data) {
        return JobPayload.builder().userIdentity(authContextService.getUserIdentity()).data(data).build();
    }
}
