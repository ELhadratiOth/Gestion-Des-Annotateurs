package com.gestiondesannotateurs.dtos;

public record CategoryWithKappaResponseDto(
        String mostFrequentCategory,
        double kappaScore,
        String agreementLevel
) {}