package com.gestiondesannotateurs.shared;
public record GlobalResponse<T>(
        Status status,
        T data,
        Erreur erreur
)   {
    public static <T> GlobalResponse<T> success(T data) {
        return new GlobalResponse<>(new Status("success"), data, null);
    }
    public static GlobalResponse<?> error(String message, String code) {
        return new GlobalResponse<>(new Status("error"), null, new Erreur(message, code));
    }
    // Records imbriqués
    public record Status(String type) {}
    public record Erreur(String message, String code) {}
}
