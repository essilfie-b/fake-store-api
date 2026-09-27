package com.supply.chain.user_management.exceptions;

import com.supply.chain.inventory.exceptions.EntityInUseException;
import com.supply.chain.inventory.exceptions.ExceptionResponse;
import com.supply.chain.inventory.exceptions.ResourceNotFoundException;
import com.supply.chain.user_management.dtos.AuthenticationResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private static final String MESSAGE_KEY = "message";
    private static final String ERROR_KEY = "error";

    @ExceptionHandler(EmailExistsException.class)
    public ResponseEntity<Map<String, String>> handleException(EmailExistsException exception) {
        var error = new HashMap<>(Map.of(MESSAGE_KEY, exception.getMessage()));
        error.put(ERROR_KEY, HttpStatus.BAD_REQUEST.getReasonPhrase());
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleException(UserNotFoundException exception) {
        var error = new HashMap<>(Map.of(MESSAGE_KEY, exception.getMessage()));
        error.put(ERROR_KEY, HttpStatus.NOT_FOUND.getReasonPhrase());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(MailException.class)
    public ResponseEntity<Map<String, String>> handleException(MailException exception) {
        var error = new HashMap<>(Map.of(MESSAGE_KEY, exception.getMessage()));
        error.put(ERROR_KEY, HttpStatus.BAD_REQUEST.getReasonPhrase());
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler({BadCredentialsException.class, UsernameNotFoundException.class, AuthenticationException.class})
    public ResponseEntity<AuthenticationResponse> handleException(Exception ignoredE) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(AuthenticationResponse.builder()
                        .error("INVALID_CREDENTIALS")
                        .message("Incorrect username or password")
                        .code(HttpStatus.UNAUTHORIZED)
                        .build()
                );
    }

    @ExceptionHandler(OperationNotPermittedException.class)
    public ResponseEntity<Map<String, String>> handleException(OperationNotPermittedException exception) {
        var error = new HashMap<>(Map.of(MESSAGE_KEY, exception.getMessage()));
        error.put(ERROR_KEY, HttpStatus.BAD_REQUEST.getReasonPhrase());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(jakarta.persistence.EntityNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleException(jakarta.persistence.EntityNotFoundException exception) {
        var error = new HashMap<>(Map.of(MESSAGE_KEY, exception.getMessage()));
        error.put(ERROR_KEY, HttpStatus.NOT_FOUND.getReasonPhrase());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleException(RuntimeException exception) {
        var error = new HashMap<>(Map.of(MESSAGE_KEY, exception.getMessage()));
        error.put(ERROR_KEY, HttpStatus.NOT_ACCEPTABLE.getReasonPhrase());
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleException(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new HashMap<>();
        errors.put(ERROR_KEY, HttpStatus.BAD_REQUEST.getReasonPhrase());

        exception.getFieldErrors().forEach(error -> {
            var name = error.getField();
            var message = error.getDefaultMessage();
            errors.put(name, message);
        });

        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(RoleAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleException(RoleAlreadyExistsException exception) {
        var error = new HashMap<>(Map.of(MESSAGE_KEY, exception.getMessage()));
        error.put(ERROR_KEY, HttpStatus.BAD_REQUEST.getReasonPhrase());
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleException(RoleNotFoundException exception) {
        var error = new HashMap<>(Map.of(MESSAGE_KEY, exception.getMessage()));
        error.put(ERROR_KEY, HttpStatus.BAD_REQUEST.getReasonPhrase());
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(RoleInUseException.class)
    public ResponseEntity<Map<String, String>> handleException(RoleInUseException exception) {
        var error = new HashMap<>(Map.of(MESSAGE_KEY, exception.getMessage()));
        error.put(ERROR_KEY, HttpStatus.BAD_REQUEST.getReasonPhrase());
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleException(ResourceNotFoundException exception) {
        return ResponseEntity.status(exception.getStatus()).body(new ExceptionResponse(exception.getMessage()));
    }

    @ExceptionHandler(EntityInUseException.class)
    public ResponseEntity<ExceptionResponse> handleException(EntityInUseException exception) {
        return ResponseEntity.badRequest().body(new ExceptionResponse(exception.getMessage()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrityViolation(
            DataIntegrityViolationException ex) {

        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("timestamp", System.currentTimeMillis());

        String message = ex.getMessage().toLowerCase();

        if (message.contains("duplicate entry") || message.contains("unique constraint")) {
            if (message.contains("product_id") && message.contains("location_id")) {
                response.put(ERROR_KEY, "DUPLICATE_INVENTORY");
                response.put(MESSAGE_KEY, "Inventory already exists for this product and location");
            } else if (message.contains("report_number")) {
                response.put(ERROR_KEY, "DUPLICATE_REPORT_NUMBER");
                response.put(MESSAGE_KEY, "Report number already exists");
            } else if (message.contains("name") && message.contains("category")) {
                response.put(ERROR_KEY, "DUPLICATE_STATUS");
                response.put(MESSAGE_KEY, "Status name already exists in this category");
            } else {
                response.put(ERROR_KEY, "DUPLICATE_ENTRY");
                response.put(MESSAGE_KEY, "A record with these details already exists");
            }
        } else if (message.contains("foreign key constraint")) {
            response.put(ERROR_KEY, "INVALID_REFERENCE");
            response.put(MESSAGE_KEY, "Referenced record does not exist");
        } else {
            response.put(ERROR_KEY, "DATA_INTEGRITY_ERROR");
            response.put(MESSAGE_KEY, "Data integrity constraint violated");
        }

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolation() {

        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put(ERROR_KEY, "VALIDATION_ERROR");
        response.put(MESSAGE_KEY, "Validation constraints violated");
        response.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
