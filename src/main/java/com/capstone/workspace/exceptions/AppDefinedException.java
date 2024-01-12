package com.capstone.workspace.exceptions;

import com.capstone.workspace.interfaces.ErrorCode;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.springframework.http.HttpStatus;

@Builder
@Data
public class AppDefinedException extends RuntimeException {
    @NonNull
    private ErrorCode errorCode;

    private HttpStatus httpStatus;

    private String message;
}
