package com.declutter.dclutter.exception;

import com.declutter.dclutter.dto.APIResponse;
import com.declutter.dclutter.dto.ValidationErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Handle Resource Not Found Exception
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<APIResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex) {

        APIResponse response = new APIResponse(ex.getMessage(), false);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    // Handle API Exception
    @ExceptionHandler(APIException.class)
    public ResponseEntity<APIResponse> handleAPIException(APIException ex) {

        APIResponse response = new APIResponse(ex.getMessage(), false);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Handle Validation Errors (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex,
            WebRequest request) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ValidationErrorResponse errorResponse = new ValidationErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Validation failed for one or more fields",
                request.getDescription(false).replace("uri=", ""),
                errors
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // Handle All Other Exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<APIResponse> handleGlobalException(Exception ex) {

        APIResponse response = new APIResponse(ex.getMessage(), false);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}