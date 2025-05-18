package com.gestiondesannotateurs.dtos;

public record PersonDTO(
        Long id,
        String firstname,
        String lastname,
        String email,
        String username,
        String role
) {}
