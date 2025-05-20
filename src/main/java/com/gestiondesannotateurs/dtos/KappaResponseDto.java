package com.gestiondesannotateurs.dtos;

public record KappaResponseDto(
        double kappaScore,
        String interpretation,
        String reliabilityLevel
) {}