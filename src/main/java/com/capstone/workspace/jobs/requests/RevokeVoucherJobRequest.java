package com.capstone.workspace.jobs.requests;

import com.capstone.workspace.jobs.handlers.RevokeVoucherJobRequestHandler;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jobrunr.jobs.lambdas.JobRequest;

import java.util.UUID;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class RevokeVoucherJobRequest implements JobRequest {
    @NonNull
    private UUID orderId;

    @Override
    public Class<RevokeVoucherJobRequestHandler> getJobRequestHandler() {
        return RevokeVoucherJobRequestHandler.class;
    }
}
