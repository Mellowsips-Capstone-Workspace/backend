package com.capstone.workspace.dtos.notification;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Builder
@Data
public class PushNotificationDto implements Serializable {
    @NotNull
    @NotBlank
    private List<String> receivers;

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
