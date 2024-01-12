package com.capstone.workspace.jobs.requests;

import com.capstone.workspace.jobs.handlers.ApproveApplicationJobRequestHandler;
import com.capstone.workspace.models.auth.UserIdentity;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jobrunr.jobs.lambdas.JobRequest;

import java.util.UUID;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class ApproveApplicationJobRequest implements JobRequest {
    @NonNull
    private UserIdentity userIdentity;

    @NonNull
    private UUID applicationId;

    @Override
    public Class<ApproveApplicationJobRequestHandler> getJobRequestHandler() {
        return ApproveApplicationJobRequestHandler.class;
    }
}
