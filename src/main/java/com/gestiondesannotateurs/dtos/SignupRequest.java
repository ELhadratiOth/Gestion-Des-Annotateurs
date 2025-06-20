package com.gestiondesannotateurs.dtos;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SignupRequest(
        @NotNull(message = "username is required")
        @Size(min = 2, max = 50, message = "min is 2 characters and max is 50 characters")
        String username,

        @NotNull(message = "password is required")
        @Size(min = 2, max = 50, message = "min is 2 characters and max is 50 characters")
        String password,

        @NotNull(message = "token is required")
        String token

) {
}
