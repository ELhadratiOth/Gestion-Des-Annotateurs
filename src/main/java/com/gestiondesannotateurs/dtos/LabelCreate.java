package com.gestiondesannotateurs.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LabelCreate(
        @NotBlank(message = "Label is required")
        @Size(min = 3, max = 100, message = "Label must be between 3 and 100 characters")
        String name,

        @NotBlank(message = "Classes is required")
        String classes
) {
}