package com.gestiondesannotateurs.controllers;
import com.gestiondesannotateurs.dtos.AnnotatorDto;
import com.gestiondesannotateurs.dtos.AnnotatorTaskDto;
import com.gestiondesannotateurs.dtos.AnnotatorWithTaskId;
import com.gestiondesannotateurs.entities.Annotator;
import com.gestiondesannotateurs.entities.TaskToDo;
import com.gestiondesannotateurs.interfaces.AnnotatorService;

import com.gestiondesannotateurs.shared.Exceptions.GlobalSuccessHandler;
import com.gestiondesannotateurs.shared.GlobalResponse;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/annotators")
public class AnnotatorController {
    @Autowired
    private AnnotatorService annotatorService;

    @GetMapping("/{annotatorId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN','ANNOTATOR')")
    public ResponseEntity<GlobalResponse<Annotator>> getAnnotatorDetails(@PathVariable Long annotatorId) {
        Annotator annotator = annotatorService.getAnnotatorById(annotatorId);
        return GlobalSuccessHandler.success("Annotator details retrieved successfully", annotator);
    }

    @GetMapping("/last")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN','ANNOTATOR')")
    public ResponseEntity<GlobalResponse<Annotator>> getLastAnnotatorDetails() {
        Annotator annotator = annotatorService.getLastAnnotatorById();
        return GlobalSuccessHandler.success("LAst Annotator details retrieved successfully", annotator);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<GlobalResponse<List<Annotator>>> getAllAnnotatorDetails() {
        List<Annotator> annotators = annotatorService.getAllAnnotators();
        return GlobalSuccessHandler.success("All annotators retrieved successfully", annotators);
    }
    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<GlobalResponse<Annotator>> createAnnotatorDetails(@RequestBody AnnotatorDto annotator) {
        Annotator createdAnnotator = annotatorService.createAnnotator(annotator);
        return GlobalSuccessHandler.created("Annotator created successfully", createdAnnotator);
    }
    @PutMapping("/{annotatorId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN','ANNOTATOR')")
    public ResponseEntity<GlobalResponse<Annotator>> updateAnnotatorDetails(
            @PathVariable Long annotatorId,
            @RequestBody AnnotatorDto annotator) {
        Annotator updatedAnnotator = annotatorService.updateAnnotator(annotatorId, annotator);
        return GlobalSuccessHandler.success("Annotator updated successfully", updatedAnnotator);
    }
    @DeleteMapping("/{annotatorId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<GlobalResponse<String>> deleteAnnotatorDetails(@PathVariable Long annotatorId) {
        annotatorService.deleteAnnotator(annotatorId);
        return GlobalSuccessHandler.deleted("Annotateur supprimé avec succès");
    }
    @PatchMapping("/{id}/spam")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")

    public ResponseEntity<GlobalResponse<Annotator>> markAsSpammer(@PathVariable Long id) {
            annotatorService.markAsSpammer(id);
            Annotator annotator = annotatorService.getAnnotatorById(id);
            return GlobalSuccessHandler.success("Annotateur marqué comme spammeur", annotator);
    }
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")

    public ResponseEntity<GlobalResponse<Annotator>> deactivateAnnotator(@PathVariable Long id) {
            annotatorService.deactivateAnnotator(id);
            Annotator annotator = annotatorService.getAnnotatorById(id);
            return GlobalSuccessHandler.success("Annotateur désactivé", annotator);
    }
    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")

    public ResponseEntity<GlobalResponse<Annotator>> activateAnnotator(@PathVariable Long id) {
        annotatorService.activateAnnotator(id);
        Annotator annotator = annotatorService.getAnnotatorById(id);
        return GlobalSuccessHandler.success("Annotateur mmactivé", annotator);
    }
    @GetMapping("/spammers/{datasetId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<GlobalResponse<List<Annotator>>> getSpammersByDataset(@PathVariable Long datasetId) {
        List<Annotator> spammers = annotatorService.getAnnotatorSpamers(datasetId);
        return GlobalSuccessHandler.success("Spammers retrieved successfully", spammers);
    }

    @GetMapping("/dataset/{datasetId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")

    public ResponseEntity<GlobalResponse<List<AnnotatorTaskDto>>> getAnnotatorsByDataset(
            @PathVariable Long datasetId) {
        List<AnnotatorTaskDto> result = annotatorService.getAnnotatorsByDataset(datasetId);
        return GlobalSuccessHandler.success("Annotateurs et tâches récupérés", result);
    }

    @GetMapping("/search/{annotatorName}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<GlobalResponse<List<Annotator>>> getAnnotatorsByAnnotatorName(@PathVariable String annotatorName) {
        List<Annotator> annotators = annotatorService.getMatchingAnnotators(annotatorName);
        return GlobalSuccessHandler.success("Spammers retrieved successfully", annotators);
    }

}