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
import java.util.Map;
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

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<GlobalResponse<?>> handleIllegalArgument(IllegalArgumentException ex) {
        List<String> errorMessages = Collections.singletonList(ex.getMessage() != null ? ex.getMessage() : "Invalid argument provided");
        return ResponseEntity.badRequest()
                .body(GlobalResponse.error(errorMessages));
    }
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<GlobalResponse<?>> handleNullPointer(NullPointerException ex) {
        List<String> errorMessages = Collections.singletonList(ex.getMessage() != null ? ex.getMessage() : "Invalid argument provided");
        return ResponseEntity.badRequest()
                .body(GlobalResponse.error(errorMessages));

    }

    @ExceptionHandler(CustomResponseException.class)
    public ResponseEntity<GlobalResponse<?>> handleCustomResponse(CustomResponseException ex) {
        System.out.println("error message : " + ex.getMessage());

        List<String> errorMessages = Collections.singletonList(ex.getMsg() != null ? ex.getMsg() : "Invalid response provideddddd");
        return ResponseEntity.badRequest()
                .body(GlobalResponse.error(errorMessages));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<GlobalResponse<?>> badUsedKeysInBodyRequest(HttpMessageNotReadableException ex) {

        List<String> errorMessages = List.of("bad Used Keys in Body Request");
        return ResponseEntity.badRequest()
                .body(GlobalResponse.error(errorMessages));
    }
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<GlobalResponse<?>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String paramName = ex.getName();
        String value = ex.getValue() != null ? ex.getValue().toString() : "null";
        String requiredType = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown";

        String errorMessage = String.format(
                "Invalid value '%s' for parameter '%s'. Expected type: %s.",
                value, paramName, requiredType
        );

        return ResponseEntity.badRequest()
                .body(GlobalResponse.error(List.of(errorMessage)));
    }


    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<GlobalResponse<?>> badUsedKeysInBodyRequest(NoResourceFoundException ex) {

        List<String> errorMessages = List.of("Emdpoint doenst exists ");
        return ResponseEntity.badRequest()
                .body(GlobalResponse.error(errorMessages));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<GlobalResponse<?>> dataIntegrityViolationException(DataIntegrityViolationException ex) {

        List<String> errorMessages = List.of("the  inserted data violates the database constraint");
        return ResponseEntity.badRequest()
                .body(GlobalResponse.error(errorMessages));
    }


}



