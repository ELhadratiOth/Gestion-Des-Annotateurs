package com.gestiondesannotateurs.shared;

import java.util.List;

public record GlobalResponse<T>(
        String status,
        T data,
        List<String> erreurs
) {
    public static <T> GlobalResponse<T> success(T data) {
        return new GlobalResponse<>("success", data, null);
    }

    public static GlobalResponse<?> error(List<String> messages) {
        return new GlobalResponse<>("error", null, messages);
    }
}