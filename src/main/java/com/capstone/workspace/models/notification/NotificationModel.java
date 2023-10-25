package com.capstone.workspace.models.notification;

import lombok.Data;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Data
public class NotificationModel {
    private UUID id;

    private String key;

    private String subject;

    private String shortDescription;

    private String content;

    private Map<String, String> metadata;

    private Boolean isSeen;

    private Instant seenAt;

    private Instant createdAt;
}
