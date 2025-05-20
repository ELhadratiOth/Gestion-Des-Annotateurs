package com.gestiondesannotateurs.dtos;

import jakarta.validation.constraints.*;
import java.util.List;

public record KappaRequestDto(
        @NotNull(message = "Annotations cannot be null")
        @Size(min = 1, message = "At least one item required")
        List<@NotNull @Size(min = 2) List<@NotNull Integer>> annotations,

        @NotNull(message = "Number of categories cannot be null")
        @Min(value = 2, message = "At least 2 categories required")
        Integer numberOfCategories
) {}