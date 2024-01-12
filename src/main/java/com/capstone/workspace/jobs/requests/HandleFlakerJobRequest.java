package com.capstone.workspace.jobs.requests;

import com.capstone.workspace.jobs.handlers.HandleFlakerJobRequestHandler;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jobrunr.jobs.lambdas.JobRequest;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class HandleFlakerJobRequest implements JobRequest {
    @NonNull
    private String username;

    @Override
    public Class<HandleFlakerJobRequestHandler> getJobRequestHandler() {
        return HandleFlakerJobRequestHandler.class;
    }
}
