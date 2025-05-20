package com.gestiondesannotateurs.controllers;
import com.gestiondesannotateurs.dtos.CategoryWithKappaResponseDto;
import com.gestiondesannotateurs.dtos.KappaRequestDto;
import com.gestiondesannotateurs.dtos.KappaResponseDto;
import com.gestiondesannotateurs.dtos.MostFrequentCategoryRequestDto;
import com.gestiondesannotateurs.services.KappaEvaluationServiceImpl;
import com.gestiondesannotateurs.shared.GlobalResponse;
import com.gestiondesannotateurs.shared.Exceptions.GlobalSuccessHandler;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/kappa")
public class KappaEvaluationController {

    private final KappaEvaluationServiceImpl kappaService;

    @Autowired
    public KappaEvaluationController(KappaEvaluationServiceImpl kappaService) {
        this.kappaService = kappaService;
    }

    @PostMapping("/calculate")
    public ResponseEntity<GlobalResponse<KappaResponseDto>> calculateKappa(
            @RequestBody @Valid KappaRequestDto request) {
        double kappa = kappaService.calculateKappa(
                request.annotations(),
                request.numberOfCategories());
        KappaResponseDto response = new KappaResponseDto(
                kappa,
                interpretKappa(kappa),
                getReliabilityLevel(kappa)
        );
        return GlobalSuccessHandler.success(
                "Kappa score calculated successfully",
                response
        );
    }

    @PostMapping("/most-frequent-category")
    public ResponseEntity<GlobalResponse<CategoryWithKappaResponseDto>> getMostFrequentCategory(
            @RequestBody @Valid MostFrequentCategoryRequestDto request) {

        String result = kappaService.getMostFrequentCategoryWithKappa(
                request.annotations(),
                request.categoryLabels());

        try {
            // Pattern modifié pour accepter les nombres négatifs et les deux formats de décimaux
            Pattern pattern = Pattern.compile("(.+?) \\(Kappa: (-?[0-9]+(?:[.,][0-9]+)?)\\)");
            Matcher matcher = pattern.matcher(result.trim());

            if (!matcher.find()) {
                throw new IllegalStateException("Unexpected format: " + result);
            }

            // Normalisation du format numérique
            String kappaStr = matcher.group(2).replace(",", ".");
            double kappa = Double.parseDouble(kappaStr);

            return GlobalSuccessHandler.success(
                    "Analysis complete",
                    new CategoryWithKappaResponseDto(
                            matcher.group(1).trim(),
                            kappa,
                            interpretKappa(kappa)
                    )
            );
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to parse: " + result,
                    e
            );
        }
    }

    private String interpretKappa(double kappa) {
        if (kappa <= 0) return "No agreement";
        if (kappa <= 0.2) return "Slight agreement";
        if (kappa <= 0.4) return "Fair agreement";
        if (kappa <= 0.6) return "Moderate agreement";
        if (kappa <= 0.8) return "Substantial agreement";
        return "Almost perfect agreement";
    }

    private String getReliabilityLevel(double kappa) {
        if (kappa < 0.4) return "Low reliability";
        if (kappa < 0.75) return "Medium reliability";
        return "High reliability";
    }
}