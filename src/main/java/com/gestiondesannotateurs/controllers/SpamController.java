package com.gestiondesannotateurs.controllers;
import com.gestiondesannotateurs.dtos.blackListDto;
import com.gestiondesannotateurs.interfaces.SpamService;
import com.gestiondesannotateurs.shared.Exceptions.CustomResponseException;
import com.gestiondesannotateurs.shared.Exceptions.GlobalSuccessHandler;
import jakarta.validation.constraints.NotNull;
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

    @GetMapping("/detect/{annotatorId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<?> getSpammerById(@PathVariable @NotNull Long annotatorId) {
        blackListDto spammer = spamService.getSpammerById(annotatorId);
        return GlobalSuccessHandler.success("Spammer found", spammer);
    }

    @GetMapping("/scan/{datasetId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<?> scan(@PathVariable Long datasetId) {
        Map<Long, Double> annotatorWithSpamsScores = spamService.detectSpammer(datasetId);
        return GlobalSuccessHandler.success("Annotators with their spam scores", annotatorWithSpamsScores);
    }

    @GetMapping("/spammers")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<?> getAllSpammers() {
        List<blackListDto> spammers = spamService.getAllSpammers();
        return GlobalSuccessHandler.success("List of all spammers", spammers);
    }
}