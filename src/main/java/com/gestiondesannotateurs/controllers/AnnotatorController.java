package com.gestiondesannotateurs.controllers;
import com.gestiondesannotateurs.dtos.AnnotatorDto;
import com.gestiondesannotateurs.dtos.AnnotatorWithTaskId;
import com.gestiondesannotateurs.entities.Annotator;
import com.gestiondesannotateurs.entities.TaskToDo;
import com.gestiondesannotateurs.interfaces.AnnotatorService;

import com.gestiondesannotateurs.shared.Exceptions.GlobalSuccessHandler;
import com.gestiondesannotateurs.shared.GlobalResponse;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<GlobalResponse<Annotator>> getAnnotatorDetails(@PathVariable Long annotatorId) {
        Annotator annotator = annotatorService.getAnnotatorById(annotatorId);
        return GlobalSuccessHandler.success("Annotator details retrieved successfully", annotator);
    }

    @GetMapping
    public ResponseEntity<GlobalResponse<List<Annotator>>> getAllAnnotatorDetails() {
        List<Annotator> annotators = annotatorService.getAllAnnotators();
        return GlobalSuccessHandler.success("All annotators retrieved successfully", annotators);
    }

    @PostMapping
    public ResponseEntity<GlobalResponse<Annotator>> createAnnotatorDetails(@Valid @RequestBody AnnotatorDto annotator) {
        Annotator createdAnnotator = annotatorService.createAnnotator(annotator);
        return GlobalSuccessHandler.created("Annotator created successfully", createdAnnotator);
    }

    @PutMapping("/{annotatorId}")
    public ResponseEntity<GlobalResponse<Annotator>> updateAnnotatorDetails(
            @PathVariable Long annotatorId,
            @RequestBody AnnotatorDto annotator) {
        Annotator updatedAnnotator = annotatorService.updateAnnotator(annotatorId, annotator);
        return GlobalSuccessHandler.success("Annotator updated successfully", updatedAnnotator);
    }

    @DeleteMapping("/{annotatorId}")
    public ResponseEntity<GlobalResponse<String>> deleteAnnotatorDetails(@PathVariable Long annotatorId) {
        annotatorService.deleteAnnotator(annotatorId);
        return GlobalSuccessHandler.deleted("Annotateur supprimé avec succès");
    }

    @PatchMapping("/{id}/spam")
    public ResponseEntity<GlobalResponse<Annotator>> markAsSpammer(@PathVariable Long id) {
            annotatorService.markAsSpammer(id);
            Annotator annotator = annotatorService.getAnnotatorById(id);
            return GlobalSuccessHandler.success("Annotateur marqué comme spammeur", annotator);
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<GlobalResponse<Annotator>> deactivateAnnotator(@PathVariable Long id) {
            annotatorService.deactivateAnnotator(id);
            Annotator annotator = annotatorService.getAnnotatorById(id);
            return GlobalSuccessHandler.success("Annotateur désactivé", annotator);
        }


    @GetMapping("/spammers/{datasetId}")
    public ResponseEntity<GlobalResponse<List<Annotator>>> getSpammersByDataset(@PathVariable Long datasetId) {
        List<Annotator> spammers = annotatorService.getAnnotatorSpamers(datasetId);
        return GlobalSuccessHandler.success("Spammers retrieved successfully", spammers);
    }
    @GetMapping("/dataset/{datasetId}")
    public ResponseEntity<GlobalResponse<List<AnnotatorWithTaskId>>> getAnnotatorsByDataset(
            @PathVariable Long datasetId
    ) {
        List<AnnotatorWithTaskId> result = annotatorService.getAnnotatorsByDataset(datasetId);
        return GlobalSuccessHandler.success("Annotateurs et tâches récupérés", result);
    }
}