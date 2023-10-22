package com.capstone.workspace.models.notification;

import lombok.Data;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Data
public class NotificationModel {
    private UUID id;

    private String receiver;

    private String subject;

    private Map<String, String> subjectParams;

    private String description;

    private Map<String, String> descriptionParams;

    private Boolean isSeen;

    private Instant seenAt;

    private Instant createdAt;
}
