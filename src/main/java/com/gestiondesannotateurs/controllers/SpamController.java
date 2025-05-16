package com.gestiondesannotateurs.controllers;


import com.gestiondesannotateurs.dtos.AnnotationDto;
import com.gestiondesannotateurs.dtos.CoupleOfTextWithAnnotation;
import com.gestiondesannotateurs.dtos.TaskToDoDto;
import com.gestiondesannotateurs.entities.AnnotationClass;
import com.gestiondesannotateurs.entities.TaskToDo;
import com.gestiondesannotateurs.interfaces.*;
import com.gestiondesannotateurs.repositories.DatasetRepo;
import com.gestiondesannotateurs.shared.Exceptions.GlobalSuccessHandler;
import com.gestiondesannotateurs.shared.GlobalResponse;
import com.gestiondesannotateurs.utils.AdminDetectSpammers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/spams")
public class SpamController {

    @Autowired
    public SpamService spamService ;


    @Autowired
    private AdminService adminService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private AnnotationService annotationService;

    @Autowired
    private AdminDetectSpammers adminDetectSpammers;

    @GetMapping("/scan/{datasetId}")
    @PreAuthorize("hasAnyRole('SUPER-ADMIN', 'ADMIN')")
    public ResponseEntity<?> scan(@PathVariable Long datasetId){

        //1 admin check
        List<List<String>>  annotatorsAnnotation = adminDetectSpammers.detect(datasetId);

        return null;
    }


}
