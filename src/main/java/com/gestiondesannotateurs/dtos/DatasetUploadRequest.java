
package com.gestiondesannotateurs.dtos;

import jakarta.validation.constraints.*;

import org.springframework.web.multipart.MultipartFile;

public record DatasetUploadRequest(
        @NotBlank(message = "Name is required")
        @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
        String name,

        @Size(max = 500, message = "Description cannot exceed 500 characters")
        String description,

        @NotNull(message = "Label ID is required")
        @Positive(message = "Label ID must be a positive number")
        Long labelId,

        @NotBlank(message = "Size is required")
        Double sizeMB,

        @NotNull(message = "File is required")
        MultipartFile file
) {
}