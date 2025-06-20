package com.gestiondesannotateurs.controllers;

import com.gestiondesannotateurs.dtos.AnnotationDto;
import com.gestiondesannotateurs.dtos.AnnotationDtoAdmin;
import com.gestiondesannotateurs.dtos.AnnotationRequest;
import com.gestiondesannotateurs.dtos.AnnotationResponse;
import com.gestiondesannotateurs.entities.Annotator;
import com.gestiondesannotateurs.entities.Dataset;
import com.gestiondesannotateurs.entities.Othman;
import com.gestiondesannotateurs.interfaces.AnnotationService;
import com.gestiondesannotateurs.interfaces.AnnotatorService;
import com.gestiondesannotateurs.interfaces.CoupleOfTextService;
import com.gestiondesannotateurs.repositories.DatasetRepo;
import com.gestiondesannotateurs.repositories.OthmanRepo;
import com.gestiondesannotateurs.repositories.TaskToDoRepo;
import com.gestiondesannotateurs.shared.Exceptions.GlobalSuccessHandler;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @Autowired
    private OthmanRepo othmanRepo;
    @Autowired
    private TaskToDoRepo taskRepo;
    @Autowired
    private CoupleOfTextService coupleOfTextService;

    @PostMapping("/tasks/{taskId}")
    @PreAuthorize("@securityUtils.isOwnerOfAtask(#taskId)")
    public ResponseEntity<?> annotate(@Valid @RequestBody AnnotationRequest request, @PathVariable Long  taskId) {
        Annotator annotator=annotatorService.getAnnotatorByTask(taskId);
        Long annotatorId=annotator.getId();
        AnnotationDto ann=new AnnotationDto();
        ann.setCoupletextId(request.getCoupletextId());
        ann.setAnnotatorId(annotatorId);
        ann.setLabel(request.getLabel());
        annotationService.saveAnnotation(ann);
        Dataset dataset=taskRepo.findById(taskId).get().getDataset();
        coupleOfTextService.computeTrueLabelsForDataset(dataset);
        return GlobalSuccessHandler.success("Annotation Created Sucessfully",ann);
    }

    @GetMapping("/{datasetId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<?> getAnnotationForDataset(@PathVariable Long datasetId){
        List<AnnotationResponse> res=annotationService.getAnnotationsByDataset(datasetId);
        return GlobalSuccessHandler.success("Annotations found ",res);
    }

//    @GetMapping("/{datasetId}/{annotatorId}")
//    public ResponseEntity<?> getAnnotationForAnnotator(@PathVariable Long  annotatorId){
//        List<AnnotationResponse> res=annotationService.findByAnnotatorId(annotatorId);
//        return GlobalSuccessHandler.success("Annotations found",res);
//    }

    @GetMapping("/count-last-24h")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<?> getAnnotationCountLast24h() {
        return GlobalSuccessHandler.success("the number of annotations is",annotationService.getAnnotationsInLast24Hours());
    }

    @PostMapping("/{datasetId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<?> annotateByAdmin(@Valid @RequestBody AnnotationRequest request) {
        AnnotationDtoAdmin ann=new AnnotationDtoAdmin();
        ann.setCoupletextId(request.getCoupletextId());
        ann.setAnnotatorId(1L);
        ann.setLabel(request.getLabel());
        System.out.println(ann.getIsAdmin());
        annotationService.saveAnnotationAdmin(ann);
        return GlobalSuccessHandler.success("Annotation Created Sucessfully By SUPER ADMIN",ann);
    }

}
