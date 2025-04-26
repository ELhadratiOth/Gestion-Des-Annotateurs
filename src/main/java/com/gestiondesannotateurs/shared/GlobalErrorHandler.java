package com.gestiondesannotateurs.shared;

import com.gestiondesannotateurs.shared.Exceptions.AnnotatorNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalErrorHandler {

    @ExceptionHandler(AnnotatorNotFoundException.class)
    public ResponseEntity<GlobalResponse<?>> handleAnnotatorNotFound(AnnotatorNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(GlobalResponse.error(List.of(ex.getMessage())));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<GlobalResponse<?>> handleValidationErrors(MethodArgumentNotValidException ex) {
        List<String> errorMessages = ex.getFieldErrors().stream()
                .map(err -> "Field '" + err.getField() + "': " + err.getDefaultMessage())
                .collect(Collectors.toList());

        return ResponseEntity.badRequest()
                .body(GlobalResponse.error(errorMessages));
    }
}