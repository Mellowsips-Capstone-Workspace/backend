package com.capstone.workspace.exceptions;

import org.springframework.http.HttpStatus;

public class GoneException extends BaseException {
    public GoneException() {
        super(HttpStatus.GONE);
    }

    public GoneException(String message) {
        super(message, HttpStatus.GONE);
    }

    public GoneException(String message, Throwable cause) {
        super(message, cause, HttpStatus.GONE);
    }

    public GoneException(Throwable cause) {
        super(cause, HttpStatus.GONE);
    }

    public GoneException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace, HttpStatus.GONE);
    }
}
