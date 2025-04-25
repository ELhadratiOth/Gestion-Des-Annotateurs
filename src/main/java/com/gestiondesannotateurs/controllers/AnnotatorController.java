package com.gestiondesannotateurs.controllers;
import com.gestiondesannotateurs.dtos.AnnotatorDto;
import com.gestiondesannotateurs.entities.Annotator;
import com.gestiondesannotateurs.interfaces.AnnotatorService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/annotator")
public class AnnotatorController {
	@Autowired
    private  AnnotatorService annotatorService;


    @GetMapping("/{annotatorId}")
    public ResponseEntity<Annotator> getAnnotatorDetails(@PathVariable Long annotatorId) {
        return ResponseEntity.ok(annotatorService.getAnnotatorById(annotatorId));
    }

    @GetMapping
    public ResponseEntity<List<Annotator>> getAllAnnotatorDetails() {
        return ResponseEntity.ok(annotatorService.getAllAnnotators());
    }

    @PostMapping
    public ResponseEntity<Annotator> createAnnotatorDetails(@Valid @RequestBody AnnotatorDto annotator) {
    	
    	
        Annotator createdAnnotator = annotatorService.createAnnotator(annotator);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAnnotator);
    }

    @PutMapping("/{annotatorId}")
    public ResponseEntity<Annotator> updateAnnotatorDetails(
            @PathVariable Long annotatorId,
            @RequestBody AnnotatorDto annotator) {
            Annotator updatedAnnotator = annotatorService.updateAnnotator(annotatorId, annotator);
           return ResponseEntity.ok(updatedAnnotator);
    }

    @DeleteMapping("/{annotatorId}")
    public ResponseEntity<Void> deleteAnnotatorDetails(@PathVariable Long annotatorId) {
        annotatorService.deleteAnnotator(annotatorId);
        return ResponseEntity.noContent().build();
    }
}