package com.gestiondesannotateurs.controllers;
import com.gestiondesannotateurs.dtos.*;
import com.gestiondesannotateurs.entities.Annotator;
import com.gestiondesannotateurs.interfaces.AnnotatorService;

import com.gestiondesannotateurs.dtos.*;
import com.gestiondesannotateurs.shared.Exceptions.GlobalSuccessHandler;
import com.gestiondesannotateurs.shared.GlobalResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
        return GlobalSuccessHandler.success("Last Annotator details retrieved successfully", annotator);
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
        return GlobalSuccessHandler.deleted("Annotator deleted successfully");
    }

    @PatchMapping("/{id}/spam")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<GlobalResponse<Annotator>> markAsSpammer(@PathVariable Long id) {
        annotatorService.markAsSpammer(id);
        Annotator annotator = annotatorService.getAnnotatorById(id);
        return GlobalSuccessHandler.success("Annotator marked as spammer", annotator);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<GlobalResponse<Annotator>> deactivateAnnotator(@PathVariable Long id) {
        annotatorService.deactivateAnnotator(id);
        Annotator annotator = annotatorService.getAnnotatorById(id);
        return GlobalSuccessHandler.success("Annotator deactivated", annotator);
    }

    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<GlobalResponse<Annotator>> activateAnnotator(@PathVariable Long id) {
        annotatorService.activateAnnotator(id);
        Annotator annotator = annotatorService.getAnnotatorById(id);
        return GlobalSuccessHandler.success("Annotator activated", annotator);
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
        return GlobalSuccessHandler.success("Annotators and tasks retrieved", result);
    }

    @GetMapping("/search/{annotatorName}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<GlobalResponse<List<Annotator>>> getAnnotatorsByAnnotatorName(@PathVariable String annotatorName) {
        List<Annotator> annotators = annotatorService.getMatchingAnnotators(annotatorName);
        return GlobalSuccessHandler.success("Matching annotators retrieved", annotators);
    }

    @GetMapping("/global-stats")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<GlobalResponse<Map<String, Map.Entry<Long, Long>>>> getAnnotatorStats() {
        Map<String, Map.Entry<Long, Long>> stats = annotatorService.getAnnotatorsStats();
        return GlobalSuccessHandler.success("Annotator statistics", stats);
    }

    @GetMapping("/coupleoftextannotated/{annotatorId}/{taskId}")
    public ResponseEntity<GlobalResponse<List<CoupleOfTextWithAnnotation>>> getAnnotatorCoupleOfTextWithTheirAnnotationsByTaskId(@PathVariable Long annotatorId,@PathVariable Long taskId) {
        List<CoupleOfTextWithAnnotation>  coupleOfTextWithAnnotations = annotatorService.getCoupletextsWithAnnotationByAnnotator(annotatorId,taskId);
        return GlobalSuccessHandler.success(
                "Successfully retrieved task coupletexts for task id = " + taskId,
                coupleOfTextWithAnnotations
        );
    }
    @GetMapping("/getAnnotatorTasks/{annotatorId}")
    public ResponseEntity<?> getListOfTasksForAnnotator(@PathVariable Long annotatorId) {
        return GlobalSuccessHandler.success(
                "Successfully retrived list of tasks for Annotator " + annotatorId,
                annotatorService.getListOfTasksForAnnotator(annotatorId)
        );
    }

}