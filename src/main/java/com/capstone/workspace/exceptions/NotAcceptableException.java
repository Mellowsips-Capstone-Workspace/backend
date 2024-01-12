package com.capstone.workspace.exceptions;

import org.springframework.http.HttpStatus;

public class NotAcceptableException extends BaseException {
    public NotAcceptableException() {
        super(HttpStatus.NOT_ACCEPTABLE);
    }

    public NotAcceptableException(String message) {
        super(message, HttpStatus.NOT_ACCEPTABLE);
    }

    public NotAcceptableException(String message, Throwable cause) {
        super(message, cause, HttpStatus.NOT_ACCEPTABLE);
    }

    public NotAcceptableException(Throwable cause) {
        super(cause, HttpStatus.NOT_ACCEPTABLE);
    }

    public NotAcceptableException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace, HttpStatus.NOT_ACCEPTABLE);
    }
}
