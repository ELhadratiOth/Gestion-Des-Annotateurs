package com.gestiondesannotateurs.dtos;

import jakarta.validation.constraints.*;

public record DatasetUpdata(
        @NotBlank(message = "Name is required")
        @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
        String name,

        @Size(max = 500, message = "Description cannot exceed 500 characters")
        String description,

        @NotNull(message = "Label ID is required")
        @Positive(message = "Label ID must be a positive number")
        Long labelId
) {
}
