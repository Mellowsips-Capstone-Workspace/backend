package com.capstone.workspace.jobs.handlers;

import com.capstone.workspace.jobs.requests.PushNotificationJobRequest;
import com.capstone.workspace.services.notification.NotificationService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jobrunr.jobs.annotations.Job;
import org.jobrunr.jobs.lambdas.JobRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PushNotificationJobRequestHandler implements JobRequestHandler<PushNotificationJobRequest> {
    private final static Logger logger = LoggerFactory.getLogger(PushNotificationJobRequestHandler.class);

    @NonNull
    private final NotificationService notificationService;

    @Override
    @Job(name = "Push notification job")
    public void run(PushNotificationJobRequest request) {
        logger.info("Start push notification job");
        notificationService.createPrivateNotification(request.getDto());
        logger.info("Complete push notification job");
    }
}
