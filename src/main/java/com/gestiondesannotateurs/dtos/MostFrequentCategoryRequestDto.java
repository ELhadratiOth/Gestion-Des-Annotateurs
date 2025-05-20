package com.gestiondesannotateurs.dtos;
import jakarta.validation.constraints.*;
import java.util.List;
import java.util.Map;

public record MostFrequentCategoryRequestDto(
        @NotNull(message = "Annotations cannot be null")
        @Size(min = 2, message = "At least 2 annotations required")
        List<@NotNull Integer> annotations,

        @NotNull(message = "Category labels cannot be null")
        @Size(min = 2, message = "At least 2 categories required")
        Map<@NotNull Integer, @NotBlank String> categoryLabels
) {}