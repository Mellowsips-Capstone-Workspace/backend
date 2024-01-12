package com.capstone.workspace.models.notification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationModel {
    private UUID id;

    private String key;

    private String subject;

    private String shortDescription;

    private String content;

    private Map<String, Object> metadata;

    private Boolean isSeen;

    private Instant seenAt;

    private Instant createdAt;
}
