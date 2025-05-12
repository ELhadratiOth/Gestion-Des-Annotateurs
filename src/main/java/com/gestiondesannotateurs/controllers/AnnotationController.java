package com.gestiondesannotateurs.controllers;

import com.gestiondesannotateurs.dtos.AnnotationDto;
import com.gestiondesannotateurs.dtos.AnnotationRequest;
import com.gestiondesannotateurs.entities.Annotator;
import com.gestiondesannotateurs.interfaces.AnnotationService;
import com.gestiondesannotateurs.interfaces.AnnotatorService;
import com.gestiondesannotateurs.shared.Exceptions.GlobalSuccessHandler;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/annotations")
public class AnnotationController {
    @Autowired
    private AnnotationService annotationService;
    @Autowired
    private AnnotatorService annotatorService;
    @PostMapping()
    public ResponseEntity<?> annotate(@Valid @RequestBody AnnotationRequest request, Authentication authentication) {
        String email =authentication.getName();
        Annotator annotator=annotatorService.getAnnotatorByEmail(email);
        Long annotatorId=annotator.getId();
        AnnotationDto ann=new AnnotationDto();
        ann.setCoupletextId(request.getCoupletextId());
        ann.setAnnotatorId(annotatorId);
        ann.setLabel(request.getLabel());
        annotationService.saveAnnotation(ann);
        return GlobalSuccessHandler.success("Annotation Created Sucessfully",ann);
    }

    @GetMapping("/datasets/{datasetId}")
    public ResponseEntity<?> getAnnotationForDataset(@PathVariable Long datasetId){
        List<AnnotationDto> res=annotationService.getAnnotationsByDataset(datasetId);

        return GlobalSuccessHandler.success("Annotations found",res);

    }


}
