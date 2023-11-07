package com.capstone.workspace.jobs.requests;

import com.capstone.workspace.dtos.notification.PushNotificationDto;
import com.capstone.workspace.entities.order.Order;
import com.capstone.workspace.jobs.handlers.PushNotificationJobRequestHandler;
import com.capstone.workspace.jobs.handlers.PushNotificationOrderChangesJobRequestHandler;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jobrunr.jobs.lambdas.JobRequest;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class PushNotificationOrderChangesJobRequest implements JobRequest {
    @NonNull
    private Order order;

    @Override
    public Class<PushNotificationOrderChangesJobRequestHandler> getJobRequestHandler() {
        return PushNotificationOrderChangesJobRequestHandler.class;
    }
}
