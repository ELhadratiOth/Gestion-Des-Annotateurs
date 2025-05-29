package com.gestiondesannotateurs.shared;

import com.gestiondesannotateurs.shared.Exceptions.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalErrorHandler {

    @ExceptionHandler(AnnotatorNotFoundException.class)
    public ResponseEntity<GlobalResponse<?>> handleAnnotatorNotFound(AnnotatorNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(GlobalResponse.error(List.of(ex.getMessage())));
    }

    @ExceptionHandler(CustomResponseException.class)
    public ResponseEntity<GlobalResponse<?>> handleCustomResponse(CustomResponseException ex) {
        HttpStatus status = HttpStatus.valueOf(ex.getCode());
        return ResponseEntity.status(status)
                .body(GlobalResponse.error(List.of(ex.getMsg())));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<GlobalResponse<?>> handleValidationErrors(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.toList());
        return ResponseEntity.badRequest()
                .body(GlobalResponse.error(errors));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<GlobalResponse<?>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(GlobalResponse.error(List.of("Database constraint violation")));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<GlobalResponse<?>> handleGenericException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(GlobalResponse.error(List.of("Internal server error")));
    }
}