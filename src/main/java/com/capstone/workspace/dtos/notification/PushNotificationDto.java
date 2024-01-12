package com.capstone.workspace.dtos.notification;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PushNotificationDto implements Serializable {
    @NotNull
    @NotBlank
    private List<String> receivers;

    @NotNull
    @NotBlank
    @Size(max = 255)
    private String key;

    @NotNull
    @NotBlank
    @Size(max = 255)
    private String subject;

    @Size(max = 255)
    private String shortDescription;

    @NotBlank
    private String content;

    private Map<String, Object> metadata;
}
