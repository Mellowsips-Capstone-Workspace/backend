package com.capstone.workspace.services.shared;

import com.capstone.workspace.jobs.requests.ApproveApplicationJobRequest;
import com.capstone.workspace.services.auth.IdentityService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jobrunr.scheduling.BackgroundJobRequest;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JobService {
    @NonNull
    private final IdentityService identityService;

    public void publishApprovedApplicationJob(UUID applicationId) {
        BackgroundJobRequest.enqueue(
            new ApproveApplicationJobRequest(
                identityService.getUserIdentity(),
                applicationId
            )
        );
    }
}
