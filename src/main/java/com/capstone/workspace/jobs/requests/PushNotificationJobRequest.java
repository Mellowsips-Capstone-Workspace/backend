package com.capstone.workspace.jobs.requests;

import com.capstone.workspace.dtos.notification.PushNotificationDto;
import com.capstone.workspace.jobs.handlers.PushNotificationJobRequestHandler;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jobrunr.jobs.lambdas.JobRequest;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class PushNotificationJobRequest implements JobRequest {
    @NonNull
    private PushNotificationDto dto;

    @Override
    public Class<PushNotificationJobRequestHandler> getJobRequestHandler() {
        return PushNotificationJobRequestHandler.class;
    }
}
