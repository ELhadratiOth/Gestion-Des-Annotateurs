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
    private AdminDetectSpammers adminDetectSpammers;

    @Autowired
    private DetectSpamersByIncoherence detectSpamersByIncoherence;


    @GetMapping("/scan/{datasetId}")
    @PreAuthorize("hasAnyRole('SUPER-ADMIN', 'ADMIN')")
    public ResponseEntity<?> scan(@PathVariable Long datasetId){

        //1 admin check
        Map<Long, Double>  annotatorsAnnotation = adminDetectSpammers.detect(datasetId);

        Map<Long, Double> inconsistencies=detectSpamersByIncoherence.detectAllSpammersByInconsistency(datasetId);
        Map<Long, Double> averagedScores = new HashMap<>();

        // compute the averge spamscore
        for (Map.Entry<Long, Double> entry : annotatorsAnnotation.entrySet()) {
            Long annotatorId = entry.getKey();
            Double score1 = entry.getValue();
            Double score2 = inconsistencies.get(annotatorId);

            if (score2 != null) { // S'assurer que l'annotateur existe aussi dans la 2e map
                double average = (score1 + score2) / 2.0;
                averagedScores.put(annotatorId, average);
            } else {
                throw new CustomResponseException(404, "Error one annotator not in the two map");

            }

        }
        return GlobalSuccessHandler.success("spams score of annotators",averagedScores);

    }


}
