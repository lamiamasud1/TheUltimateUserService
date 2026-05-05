package com.example.theultimateuser.advice;

import com.example.theultimateuser.service.ImmutableFieldUpdateException;
import com.example.theultimateuser.service.InvalidSearchCriteriaException;
import com.example.theultimateuser.service.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Centralized error handling component
 * Provides a unified JSON response structure to ensure consistent API error reporting
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUserNotFound(UserNotFoundException e) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequest(MethodArgumentTypeMismatchException e) {
        String message = "Invalid input for field: " + e.getName();
        if (e.getRequiredType() != null && e.getRequiredType().equals(LocalDate.class)) {
            message = "Invalid date format. Please use YYYY-MM-DD";
        }
        return buildErrorResponse(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(InvalidSearchCriteriaException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidSearchCriteria(InvalidSearchCriteriaException e) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
    }


    @ExceptionHandler(ImmutableFieldUpdateException.class)
    public ResponseEntity<Map<String, Object>> handleImmutableFieldUpdateException(ImmutableFieldUpdateException e) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralExceptions(Exception e) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }

    private ResponseEntity<Map<String, Object>> buildErrorResponse(HttpStatus status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        return new ResponseEntity<>(body, status);
    }

}