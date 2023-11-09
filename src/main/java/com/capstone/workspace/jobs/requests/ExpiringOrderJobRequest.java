package com.capstone.workspace.jobs.requests;

import com.capstone.workspace.jobs.handlers.ExpiringOrderJobRequestHandler;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jobrunr.jobs.lambdas.JobRequest;

import java.util.UUID;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class ExpiringOrderJobRequest implements JobRequest {
    @NonNull
    private UUID orderId;

    @Override
    public Class<ExpiringOrderJobRequestHandler> getJobRequestHandler() {
        return ExpiringOrderJobRequestHandler.class;
    }
}
