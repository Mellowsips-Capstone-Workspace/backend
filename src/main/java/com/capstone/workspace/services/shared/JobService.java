package com.capstone.workspace.services.shared;

import com.capstone.workspace.services.application.ApplicationService;
import com.capstone.workspace.services.auth.AuthContextService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jobrunr.scheduling.JobScheduler;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class JobService {
    @NonNull
    private final JobScheduler jobScheduler;

    @NonNull
    private final AuthContextService authContextService;

    @NonNull
    private final ApplicationService applicationService;

    public void publishApprovedApplicationJob(Map<String, Object> payload) {
        jobScheduler.enqueue(() -> applicationService.approveApplication());
    }
}
