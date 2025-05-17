package com.gestiondesannotateurs.controllers;

import com.gestiondesannotateurs.services.KappaEvaluationServiceImpl;
import com.gestiondesannotateurs.shared.GlobalResponse;
import com.gestiondesannotateurs.shared.Exceptions.GlobalSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/kappa")
public class KappaEvaluationController {

    @Autowired
    private KappaEvaluationServiceImpl kappaService;

    @PostMapping("/calculate")
    public ResponseEntity<GlobalResponse<Double>> calculateKappa(
            @RequestBody KappaRequest request) {
        double kappa = kappaService.calculateKappa(request.getAnnotations(), request.getNumberOfCategories());
        return GlobalSuccessHandler.success("Kappa score calculated successfully", kappa);
    }

    public static class KappaRequest {
        private List<List<Integer>> annotations;
        private Integer numberOfCategories;

        // Getters and Setters
        public List<List<Integer>> getAnnotations() {
            return annotations;
        }

        public void setAnnotations(List<List<Integer>> annotations) {
            this.annotations = annotations;
        }

        public Integer getNumberOfCategories() {
            return numberOfCategories;
        }

        public void setNumberOfCategories(Integer numberOfCategories) {
            this.numberOfCategories = numberOfCategories;
        }
    }
}