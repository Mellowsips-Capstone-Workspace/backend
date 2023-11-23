package com.capstone.workspace.exceptions;

import org.springframework.http.HttpStatus;

public class ServiceUnavailableException extends BaseException {
    public ServiceUnavailableException() {
        super(HttpStatus.SERVICE_UNAVAILABLE);
    }

    public ServiceUnavailableException(String message) {
        super(message, HttpStatus.SERVICE_UNAVAILABLE);
    }

    public ServiceUnavailableException(String message, Throwable cause) {
        super(message, cause, HttpStatus.SERVICE_UNAVAILABLE);
    }

    public ServiceUnavailableException(Throwable cause) {
        super(cause, HttpStatus.SERVICE_UNAVAILABLE);
    }

    public ServiceUnavailableException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace, HttpStatus.SERVICE_UNAVAILABLE);
    }
}
