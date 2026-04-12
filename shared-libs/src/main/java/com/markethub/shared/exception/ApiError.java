package com.markethub.shared.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiError(
        int status,
        String error,
        String message,
        String errorCode,
        Instant timestamp,
        List<FieldError> fieldErrors
) {
    public record FieldError(String field, String message) {}

    public static ApiError of(HttpStatus status, String message, String errorCode) {
        return new ApiError(status.value(), status.getReasonPhrase(), message, errorCode, Instant.now(), null);
    }

    public static ApiError ofValidation(HttpStatus status, List<FieldError> fieldErrors) {
        return new ApiError(status.value(), status.getReasonPhrase(), "Validation failed", "VALIDATION_ERROR", Instant.now(), fieldErrors);
    }
}