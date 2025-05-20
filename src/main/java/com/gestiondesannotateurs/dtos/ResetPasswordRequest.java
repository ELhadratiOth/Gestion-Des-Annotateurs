package com.gestiondesannotateurs.dtos;

public record ResetPasswordRequest(
        String token,
        String newPassword
) {
}