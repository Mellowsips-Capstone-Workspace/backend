package com.capstone.workspace.interfaces;

import org.springframework.http.HttpStatus;

public interface ErrorCode {
    String getValue();
    HttpStatus getHttpStatus();
    String getMessage();
}
