package com.capstone.workspace.controllers.notification;

import com.capstone.workspace.dtos.notification.SearchNotificationDto;
import com.capstone.workspace.entities.notification.Notification;
import com.capstone.workspace.models.notification.NotificationModel;
import com.capstone.workspace.models.shared.PaginationResponseModel;
import com.capstone.workspace.models.shared.ResponseModel;
import com.capstone.workspace.services.notification.NotificationService;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping( "/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    @NonNull
    private final NotificationService notificationService;

    @NonNull
    private final ModelMapper mapper;

    @PostMapping("/search")
    public ResponseModel<PaginationResponseModel<NotificationModel>> search(@Valid @RequestBody SearchNotificationDto dto) {
        PaginationResponseModel<NotificationModel> data = notificationService.search(dto);
        return ResponseModel.<PaginationResponseModel<NotificationModel>>builder().data(data).build();
    }

    @PutMapping("/{id}/mark-as-read")
    public ResponseModel<NotificationModel> markAsRead(@PathVariable UUID id) {
        Notification entity = notificationService.markAsRead(id);
        NotificationModel model = mapper.map(entity, NotificationModel.class);
        return ResponseModel.<NotificationModel>builder().data(model).build();
    }

    @PutMapping("/mark-all-as-read")
    public ResponseModel markAllAsRead() {
        notificationService.markAllAsRead();
        return ResponseModel.builder().message("Mark all your notification as read successfully").build();
    }
}
