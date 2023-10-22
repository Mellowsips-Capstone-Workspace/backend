package com.capstone.workspace.dtos.notification;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Builder
@Data
public class PushNotificationDto {
    @NotNull
    @NotBlank
    private String receiver;

    @NotNull
    @NotBlank
    private String subject;

    private Map<String, String> subjectParams;

    @Size(max = 255)
    private String shortDescription;

    private Map<String, String> shortDescriptionParams;

    @NotNull
    @NotBlank
    private String content;

    private Map<String, String> contentParams;
}
