package com.gestiondesannotateurs.controllers;


import com.gestiondesannotateurs.dtos.AnnotationDto;
import com.gestiondesannotateurs.dtos.CoupleOfTextWithAnnotation;
import com.gestiondesannotateurs.dtos.TaskToDoDto;
import com.gestiondesannotateurs.entities.AnnotationClass;
import com.gestiondesannotateurs.entities.TaskToDo;
import com.gestiondesannotateurs.interfaces.*;
import com.gestiondesannotateurs.repositories.DatasetRepo;
import com.gestiondesannotateurs.shared.Exceptions.CustomResponseException;
import com.gestiondesannotateurs.shared.Exceptions.GlobalSuccessHandler;
import com.gestiondesannotateurs.shared.GlobalResponse;
import com.gestiondesannotateurs.utils.AdminDetectSpammers;
import com.gestiondesannotateurs.utils.DetectSpamersByIncoherence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/spams")
public class SpamController {
    @Autowired
    private SpamService spamService;
    @GetMapping("/scan/{datasetId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<?> scan(@PathVariable Long datasetId){
        Map<Long, Double> annotatorWithspamsScores=spamService.detectSpammer(datasetId);
        return GlobalSuccessHandler.success("Annotators with theirs sapms score",annotatorWithspamsScores);
    }

}
