package com.capstone.workspace.exceptions;

import org.springframework.http.HttpStatus;

public class TooManyRequestsException extends BaseException {
    public TooManyRequestsException() {
        super(HttpStatus.TOO_MANY_REQUESTS);
    }

    public TooManyRequestsException(String message) {
        super(message, HttpStatus.TOO_MANY_REQUESTS);
    }

    public TooManyRequestsException(String message, Throwable cause) {
        super(message, cause, HttpStatus.TOO_MANY_REQUESTS);
    }

    public TooManyRequestsException(Throwable cause) {
        super(cause, HttpStatus.TOO_MANY_REQUESTS);
    }

    public TooManyRequestsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace, HttpStatus.TOO_MANY_REQUESTS);
    }
}
