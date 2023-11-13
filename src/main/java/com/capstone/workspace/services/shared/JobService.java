package com.capstone.workspace.services.shared;

import com.capstone.workspace.dtos.notification.PushNotificationDto;
import com.capstone.workspace.entities.order.Order;
import com.capstone.workspace.jobs.requests.ApproveApplicationJobRequest;
import com.capstone.workspace.jobs.requests.ExpiringOrderJobRequest;
import com.capstone.workspace.jobs.requests.PushNotificationJobRequest;
import com.capstone.workspace.jobs.requests.PushNotificationOrderChangesJobRequest;
import com.capstone.workspace.models.order.OrderModel;
import com.capstone.workspace.services.auth.IdentityService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jobrunr.scheduling.BackgroundJobRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;
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

    public void publishPushNotificationJob(PushNotificationDto dto) {
        BackgroundJobRequest.enqueue(
            new PushNotificationJobRequest(dto)
        );
    }

    public void publishPushNotificationOrderChangesJob(OrderModel order) {
        BackgroundJobRequest.enqueue(
            new PushNotificationOrderChangesJobRequest(order)
        );
    }

    public void expiringOrder(UUID id) {
        BackgroundJobRequest.schedule(Instant.now().plusSeconds(900L), new ExpiringOrderJobRequest(id));
    }
}
