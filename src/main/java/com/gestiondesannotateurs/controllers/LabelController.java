package com.gestiondesannotateurs.controllers;


import com.gestiondesannotateurs.dtos.LabelCreate;
import com.gestiondesannotateurs.dtos.LabelResponse;
import com.gestiondesannotateurs.entities.Label;
import com.gestiondesannotateurs.interfaces.LabelService;
import com.gestiondesannotateurs.shared.Exceptions.GlobalSuccessHandler;
import com.gestiondesannotateurs.shared.GlobalResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/labels")
public class LabelController {
    @Autowired
    private LabelService labelService;


    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN', 'ROLE_ADMIN')")

    public ResponseEntity<GlobalResponse<List<LabelResponse>>> getAllLabels() {
        List<LabelResponse> labels = labelService.getAll();
        return GlobalSuccessHandler.success("Liste des labels récupérée avec succès", labels);
    }
    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN', 'ROLE_ADMIN')")

    public ResponseEntity<GlobalResponse<LabelResponse>> createLabel(
            @RequestBody @Valid LabelCreate labelCreate) {
        System.out.println(labelCreate);
        Label label = labelService.createLabel(labelCreate);
        LabelResponse response = new LabelResponse(label.getId(), label.getName() , label.getClasses());

        return GlobalSuccessHandler.created("Label créé avec succès", response);
    }

    @DeleteMapping("/{idLabel}")
    @PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN', 'ROLE_ADMIN')")
    public ResponseEntity<GlobalResponse<String>> deleteLabel(@PathVariable Long idLabel) {
        labelService.deleteLabel(idLabel);
        return GlobalSuccessHandler.deleted("label supprimé avec succès");
    }


}
