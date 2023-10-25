package com.capstone.workspace.controllers.notification;

import com.capstone.workspace.services.notification.NotificationService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class NotificationSocketController {
    @NonNull
    private final NotificationService notificationService;

    @NonNull
    private final ModelMapper mapper;

//    @MessageMapping("/push-notification")
//    @SendTo("/topic/notifications")
//    public NotificationModel pushNotification(@Valid PushNotificationDto dto) {
//        Notification entity = notificationService.create(dto);
//        return mapper.map(entity, NotificationModel.class);
//    }
}
