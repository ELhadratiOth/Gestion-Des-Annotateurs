package com.gestiondesannotateurs.shared;

import com.gestiondesannotateurs.shared.Exceptions.AnnotatorNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalErrorHandler {

    @ExceptionHandler(AnnotatorNotFoundException.class)
    public ResponseEntity<GlobalResponse<?>> handleAnnotatorNotFound(AnnotatorNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(GlobalResponse.error(ex.getMessage(), "ANNOTATOR_NOT_FOUND"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<GlobalResponse<?>> handleValidationErrors(MethodArgumentNotValidException ex) {
        String errorMsg = ex.getFieldErrors().stream()
                .findFirst()
                .map(err -> "Champ '" + err.getField() + "' " + err.getDefaultMessage())
                .orElse("Validation failed");

        return ResponseEntity.badRequest()
                .body(GlobalResponse.error(errorMsg, "VALIDATION_ERROR"));
    }
}