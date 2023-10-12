package com.capstone.workspace.handlers;

import com.capstone.workspace.exceptions.AppDefinedException;
import com.capstone.workspace.exceptions.BaseException;
import com.capstone.workspace.exceptions.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.ArrayList;
import java.util.Arrays;

@ControllerAdvice
public class ExternalExceptionHandler {
    private static Logger logger = LoggerFactory.getLogger(ExternalExceptionHandler.class);

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeExceptions(BaseException ex, WebRequest request) {
        logger.error(ex.getClass().getName() + ": " + ex.getMessage());
        logger.error(Arrays.toString(ex.getStackTrace()));

        HttpStatus httpStatus = ex.getHttpStatus() != null ? ex.getHttpStatus() : HttpStatus.INTERNAL_SERVER_ERROR;

        ErrorResponse errorResponse = new ErrorResponse(
                httpStatus.value(),
                ex.getMessage() != null ? ex.getMessage() : "Internal Server Error",
                null
        );

        return new ResponseEntity<>(errorResponse, httpStatus);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeExceptions(HttpMessageNotReadableException ex, WebRequest request) {
        logger.error(ex.getClass().getName() + ": " + ex.getMessage());
        logger.error(Arrays.toString(ex.getStackTrace()));

        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

        ErrorResponse errorResponse = new ErrorResponse(
                httpStatus.value(),
                "Request body not readable",
                null
        );

        return new ResponseEntity<>(errorResponse, httpStatus);
    }

    @ExceptionHandler(AppDefinedException.class)
    public ResponseEntity<ErrorResponse> handleBuilderExceptions(AppDefinedException ex, WebRequest request) {
        String message = ex.getMessage() != null ? ex.getMessage() : ex.getErrorCode().getMessage();
        HttpStatus httpStatus = ex.getHttpStatus() != null ? ex.getHttpStatus() : ex.getErrorCode().getHttpStatus();

        logger.error(ex.getClass().getName() + ": " + message);
        logger.error(Arrays.toString(ex.getStackTrace()));

        ErrorResponse errorResponse = new ErrorResponse(
                httpStatus.value(),
                message,
                ex.getErrorCode().getValue()
        );

        return new ResponseEntity<>(errorResponse, httpStatus);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleBadIncomingDataExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        Object[] detailMessageArguments = ex.getDetailMessageArguments();

        String errorMessage = null;
        for (Object detailMessageArgument: detailMessageArguments) {
            if (!((ArrayList) detailMessageArgument).isEmpty()) {
                errorMessage = ((ArrayList) detailMessageArgument).get(0).toString().replaceAll("[:']", "");
                break;
            }
        }

        logger.error(ex.getClass().getName() + ": " + ex.getMessage());
        logger.error(Arrays.toString(ex.getStackTrace()));

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                errorMessage,
                null
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleBadIncomingDataExceptions(ConstraintViolationException ex, WebRequest request) {
        String[] errorMessages = ex.getMessage().split(", ");
        String errorMessage = errorMessages.length == 0 ? null : errorMessages[0];
        if (errorMessage != null) {
            errorMessage = errorMessage.replace(":", "");
        }

        logger.error(ex.getClass().getName() + ": " + ex.getMessage());
        logger.error(Arrays.toString(ex.getStackTrace()));

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                errorMessage,
                null
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TransactionSystemException.class)
    public ResponseEntity<ErrorResponse> handleTransactionSystemException(TransactionSystemException ex, WebRequest request) {
        Throwable exception = ex.getCause().getCause() == null ? ex : ex.getCause().getCause();

        logger.error(exception.getClass().getName() + ": " + exception.getMessage());
        logger.error(Arrays.toString(exception.getStackTrace()));

        HttpStatus httpStatus = exception instanceof BaseException ? ((BaseException) exception).getHttpStatus() : HttpStatus.INTERNAL_SERVER_ERROR;

        ErrorResponse errorResponse = new ErrorResponse(
                httpStatus.value(),
                exception.getMessage(),
                null
        );

        return new ResponseEntity<>(errorResponse, httpStatus);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleOtherExceptions(Exception ex, WebRequest request) {
        logger.error(ex.getClass().getName() + ": " + ex.getMessage());
        logger.error(Arrays.toString(ex.getStackTrace()));

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                null
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}