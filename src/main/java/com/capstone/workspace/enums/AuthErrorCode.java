package com.capstone.workspace.enums;

import com.capstone.workspace.interfaces.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum AuthErrorCode implements ErrorCode {
    MISSING_TOKEN("auth/missing-token", HttpStatus.UNAUTHORIZED, "Confirmation code has expired"),
    INVALID_ACCESS_TOKEN("auth/invalid-access-token", HttpStatus.UNAUTHORIZED, "Access token is invalid or expired"),
    CODE_EXPIRED("auth/code-expired", HttpStatus.GONE, "Confirmation code has expired"),
    CODE_MISMATCH("auth/code-mismatch", HttpStatus.BAD_REQUEST, "Confirmation code does not match"),
    VERIFICATION_LIMIT_EXCEED("auth/verification-limit-exceed", HttpStatus.TOO_MANY_REQUESTS, "Exceeded verification attempts"),
    USER_NOT_FOUND("auth/user-not-found", HttpStatus.NOT_FOUND, "User not found"),
    USER_NOT_CONFIRMED("auth/user-not-confirmed", HttpStatus.UNAUTHORIZED, "User is not confirmed"),
    INVALID_CREDENTIALS("auth/invalid-credentials", HttpStatus.BAD_REQUEST, "Incorrect username or password");

    private String value;
    private HttpStatus httpStatus;
    private String message;
}