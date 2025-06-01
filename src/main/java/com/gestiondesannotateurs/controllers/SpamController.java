package com.gestiondesannotateurs.controllers;

import com.gestiondesannotateurs.entities.Annotator;
import com.gestiondesannotateurs.interfaces.SpamService;
import com.gestiondesannotateurs.shared.Exceptions.GlobalSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/spams")
public class SpamController {

    @Autowired
    private SpamService spamService;

    @GetMapping("/scan/{datasetId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<?> scan(@PathVariable Long datasetId) {
        Map<Long, Double> annotatorWithSpamsScores = spamService.detectSpammer(datasetId);
        return GlobalSuccessHandler.success("Annotators with their spam scores", annotatorWithSpamsScores);
    }
    @GetMapping("/spammers")  // Changement de l'URL
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<?> getAllSpammers() {
        List<Annotator> spammers = spamService.getAllSpammers();
        return GlobalSuccessHandler.success("List of all spammers", spammers);
    }
}