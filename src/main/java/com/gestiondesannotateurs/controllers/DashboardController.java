package com.gestiondesannotateurs.controllers;

import com.gestiondesannotateurs.dtos.AnnotatorStatsDto;
import com.gestiondesannotateurs.interfaces.DashboardService;
import com.gestiondesannotateurs.shared.Exceptions.AnnotatorNotFoundException;
import com.gestiondesannotateurs.shared.Exceptions.CustomResponseException;
import com.gestiondesannotateurs.shared.GlobalResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/annotator-stats/{annotatorId}")
    @PreAuthorize("hasAnyRole('ANNOTATOR')")
    public ResponseEntity<GlobalResponse<AnnotatorStatsDto>> getAnnotatorStats(
            @PathVariable Long annotatorId) {
        try {
            AnnotatorStatsDto stats = dashboardService.getAnnotatorStats(annotatorId);
            return ResponseEntity.ok(GlobalResponse.success(stats));
        } catch (AnnotatorNotFoundException e) {
            throw new CustomResponseException(404, e.getMessage());
        }
    }
}