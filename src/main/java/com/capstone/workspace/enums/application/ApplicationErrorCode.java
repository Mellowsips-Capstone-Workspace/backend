package com.capstone.workspace.enums.application;

import com.capstone.workspace.interfaces.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ApplicationErrorCode implements ErrorCode {
    STATUS_NOT_ALLOWED("application/status-not-allowed", HttpStatus.NOT_ACCEPTABLE, "Application status is not allowed"),
    ORGANIZATION_ALREADY_EXIST("application/organization-already-exist", HttpStatus.CONFLICT, "You are already in an organization"),
    ORGANIZATION_NOT_EXIST_YET("application/organization-not-exist-yet", HttpStatus.NOT_ACCEPTABLE, "You must be in an organization");

    private String value;
    private HttpStatus httpStatus;
    private String message;
}
