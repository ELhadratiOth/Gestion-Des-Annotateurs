package com.gestiondesannotateurs.shared;

import java.util.List;
import java.util.Map;

public record GlobalResponse<T>(
        String status,
        String message,  // Ajout du champ message
        T data,
        List<String> erreurs
) {
    public static <T> GlobalResponse<T> success(T data) {
        return new GlobalResponse<>("success", null, data, null);
    }

    public static <T> GlobalResponse<T> success(String message, T data) {
        return new GlobalResponse<>("success", message, data, null);
    }

    public static GlobalResponse<?> error(List<String> messages) {
        return new GlobalResponse<>("error", null, null, messages);
    }

    public static <E, K, V> Object error(List<E> message, Map<K, V> invalidValue) {
        return null;
    }
}