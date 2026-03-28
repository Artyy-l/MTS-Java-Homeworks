package com.mipt.todolist.exception;

import com.mipt.todolist.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String PROFILE_PROD = "prod";

    @Autowired
    private Environment environment;

    private boolean isProdProfile() {
        for (String p : environment.getActiveProfiles()) {
            if (PROFILE_PROD.equalsIgnoreCase(p)) {
                return true;
            }
        }
        return false;
    }

    private ErrorResponse baseError(HttpStatus status, String message, HttpServletRequest request, Throwable ex) {
        ErrorResponse body = new ErrorResponse();
        body.setTimestamp(Instant.now());
        body.setStatus(status.value());
        body.setError(status.getReasonPhrase());
        body.setMessage(message != null ? message : status.getReasonPhrase());
        body.setPath(request.getRequestURI());
        Map<String, Object> details = new HashMap<>();
        if (ex != null && !isProdProfile()) {
            details.put("exception", ex.getClass().getName());
        }
        body.setDetails(details.isEmpty() ? null : details);
        return body;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(fe.getField(), fe.getDefaultMessage() != null ? fe.getDefaultMessage() : "invalid");
        }
        ErrorResponse body = baseError(HttpStatus.BAD_REQUEST, "Validation failed", request, null);
        Map<String, Object> details = new HashMap<>();
        details.put("fieldErrors", fieldErrors);
        body.setDetails(details);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex,
                                                                   HttpServletRequest request) {
        Map<String, String> violations = new HashMap<>();
        for (ConstraintViolation<?> v : ex.getConstraintViolations()) {
            violations.put(v.getPropertyPath().toString(), v.getMessage());
        }
        ErrorResponse body = baseError(HttpStatus.BAD_REQUEST, "Constraint violation", request, null);
        body.setDetails(Map.of("violations", violations));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParam(MissingServletRequestParameterException ex,
                                                            HttpServletRequest request) {
        ErrorResponse body = baseError(HttpStatus.BAD_REQUEST, ex.getMessage(), request, null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleNotReadable(HttpMessageNotReadableException ex,
                                                           HttpServletRequest request) {
        ErrorResponse body = baseError(HttpStatus.BAD_REQUEST, "Malformed JSON request", request, null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoHandler(NoHandlerFoundException ex, HttpServletRequest request) {
        ErrorResponse body = baseError(HttpStatus.NOT_FOUND, "No handler for " + request.getRequestURI(), request, null);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResource(NoResourceFoundException ex, HttpServletRequest request) {
        ErrorResponse body = baseError(HttpStatus.NOT_FOUND, ex.getMessage(), request, null);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTaskNotFound(TaskNotFoundException ex, HttpServletRequest request) {
        ErrorResponse body = baseError(HttpStatus.NOT_FOUND, ex.getMessage(), request, null);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(AttachmentNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAttachmentNotFound(AttachmentNotFoundException ex,
                                                                  HttpServletRequest request) {
        ErrorResponse body = baseError(HttpStatus.NOT_FOUND, ex.getMessage(), request, null);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, HttpServletRequest request) {
        ErrorResponse body = baseError(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", request, ex);
        if (!isProdProfile() && ex != null) {
            Map<String, Object> details = body.getDetails();
            if (details == null) {
                details = new HashMap<>();
            } else {
                details = new HashMap<>(details);
            }
            details.put("stackTrace", ex.toString());
            body.setDetails(details);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
