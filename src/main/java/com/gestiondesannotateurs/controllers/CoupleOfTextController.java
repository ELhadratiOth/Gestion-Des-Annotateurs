package com.gestiondesannotateurs.controllers;

import com.gestiondesannotateurs.dtos.CoupletextDto;
import com.gestiondesannotateurs.dtos.PagedCoupletextDto;
import com.gestiondesannotateurs.entities.Dataset;
import com.gestiondesannotateurs.interfaces.CoupleOfTextService;
import com.gestiondesannotateurs.shared.Exceptions.GlobalSuccessHandler;
import com.gestiondesannotateurs.shared.GlobalResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coupletexts")
@RequiredArgsConstructor
public class CoupleOfTextController {
    @Autowired
    private CoupleOfTextService coupleOfTextService;

    @GetMapping("/{datasetId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<?> getDatasetCoupleTexts(
            @PathVariable Long datasetId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Dataset dataset = new Dataset();
        dataset.setId(datasetId);

        PagedCoupletextDto results = coupleOfTextService.findDtoByDataset(
                dataset,
                PageRequest.of(page, size)
        );

        return GlobalSuccessHandler.success(
                "Couples de textes récupérés avec succès",
                results
        );
    }

    @GetMapping("/tasks/{taskId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<GlobalResponse<List<CoupletextDto>>> getTaskCoupleTexts(
            @PathVariable Long taskId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        List<CoupletextDto> results = coupleOfTextService.getCouplesByTaskPaged(
                taskId,
                PageRequest.of(page, size)
        );

        return GlobalSuccessHandler.success(
                "Couples de textes récupérés avec succès",
                results
        );
    }
}