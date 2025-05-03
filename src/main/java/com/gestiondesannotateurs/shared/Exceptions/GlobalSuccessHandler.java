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

    // Méthodes spécialisées pour vos cas d'utilisation
    public static ResponseEntity<GlobalResponse<Map<String, Object>>> createdWithId(String message, Long id, Object entity) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", message);
        response.put("id", id);
        response.put("entity", entity);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new GlobalResponse<>("success", message, response, null));
    }
    public static ResponseEntity<GlobalResponse<String>> deleted(String message) {
        return ResponseEntity
                .status(HttpStatus.OK)  // Ou NO_CONTENT si vous préférez
                .body(new GlobalResponse<>("success", message, null, null));
    }

    public static ResponseEntity<GlobalResponse<String>> simpleMessage(String message) {
        return ResponseEntity.ok(new GlobalResponse<>("success", message, null, null));
    }
}