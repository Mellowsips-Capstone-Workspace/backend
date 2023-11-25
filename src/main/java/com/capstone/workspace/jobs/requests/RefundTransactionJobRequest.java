package com.capstone.workspace.jobs.requests;

import com.capstone.workspace.jobs.handlers.RefundTransactionJobRequestHandler;
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
public class RefundTransactionJobRequest implements JobRequest {
    @NonNull
    private UUID orderId;

    @NonNull
    private UserIdentity userIdentity;

    @Override
    public Class<RefundTransactionJobRequestHandler> getJobRequestHandler() {
        return RefundTransactionJobRequestHandler.class;
    }
}
