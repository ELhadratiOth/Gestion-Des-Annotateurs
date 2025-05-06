package com.gestiondesannotateurs.shared.Exceptions;

import com.gestiondesannotateurs.shared.GlobalResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public class GlobalSuccessHandler {

    // Méthodes de base
    public static <T> ResponseEntity<GlobalResponse<T>> success(T data) {
        return ResponseEntity.ok(GlobalResponse.success(data));
    }

    public static <T> ResponseEntity<GlobalResponse<T>> success(String message, T data) {
        return ResponseEntity.ok(new GlobalResponse<>("success", message, data, null));
    }

    public static <T> ResponseEntity<GlobalResponse<T>> created(T data) {
        return ResponseEntity.status(HttpStatus.CREATED).body(GlobalResponse.success(data));
    }

    public static <T> ResponseEntity<GlobalResponse<T>> created(String message, T data) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new GlobalResponse<>("success", message, data, null));
    }

    public static ResponseEntity<GlobalResponse<Void>> noContent() {
        return ResponseEntity.noContent().build();
    }

    public static ResponseEntity<GlobalResponse<String>> deleted(String message) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new GlobalResponse<>("success", message, null, null));
    }

    public static ResponseEntity<GlobalResponse<String>> simpleMessage(String message) {
        return ResponseEntity.ok(new GlobalResponse<>("success", message, null, null));
    }
}