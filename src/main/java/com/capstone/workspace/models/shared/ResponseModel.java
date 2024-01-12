package com.capstone.workspace.models.shared;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;

@Data
@Builder
public class ResponseModel<T> {
    @Nullable
    private T data;

    @Builder.Default
    private int statusCode = HttpStatus.OK.value();

    @Nullable
    private String message;
}
