package com.gestiondesannotateurs.utils;

import com.gestiondesannotateurs.dtos.AnnotationDto;
import com.gestiondesannotateurs.dtos.CoupleOfTextWithAnnotation;
import com.gestiondesannotateurs.dtos.TaskToDoDto;
import com.gestiondesannotateurs.entities.Label;
import com.gestiondesannotateurs.interfaces.*;
import com.gestiondesannotateurs.repositories.DatasetRepo;
import com.gestiondesannotateurs.repositories.LabelRepo;
import com.gestiondesannotateurs.shared.Exceptions.CustomResponseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class AdminDetectSpammers {

    @Autowired
    private AdminService adminService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private AnnotationService annotationService;

    @Autowired
    private LabelRepo labelRepo;
    @Autowired
    private DatasetRepo datasetRepo;
    @Autowired
    private DatasetService datasetService;

    @Autowired
    private KappaEvaluationService kappaEvaluationService;




    public Map<Long, Double> detect(Long datasetId) {
        // 1. Admin check
        List<CoupleOfTextWithAnnotation> coupleOfTextAnnotated = adminService.getListOfCoupleOfTextWithThereAnnotation(datasetId);
        for (CoupleOfTextWithAnnotation coupleOfText : coupleOfTextAnnotated) {
            if (coupleOfText.annotationId() == null) {
                throw new CustomResponseException(404, "The dataset is not fully annotated by the admin. Please annotate it first to calculate the agreement");
            }
        }

        // 2. Get annotator IDs
        List<TaskToDoDto> tasks = taskService.getTasksByDatasetId(datasetId);
        List<Long> annotatorIds = tasks.stream()
                .map(TaskToDoDto::annotatorId)
                .collect(Collectors.toList());

        // 3. Collect annotations
        List<List<String>> allAnnotations = new ArrayList<>();
        for (CoupleOfTextWithAnnotation coupleOfText : coupleOfTextAnnotated) {
            List<String> annotationForCoupleOfText = new ArrayList<>();
            annotationForCoupleOfText.add(coupleOfText.annotationLabel());
            for (Long annotatorId : annotatorIds) {

                AnnotationDto annotationDto = annotationService.findByAnnotationIdSharedWithAdmin(annotatorId, coupleOfText.coupleOfTextId());
                annotationForCoupleOfText.add(annotationDto.getLabel() != null ? annotationDto.getLabel() : "not annotated");
            }
            allAnnotations.add(annotationForCoupleOfText);
        }

//        System.out.println(allAnnotations);

        var allAnnotationsT=  AnnotationTransformer.transformAnnotations(allAnnotations);
        System.out.println(allAnnotationsT);
        Long labelId = datasetService.findDatasetById(datasetId).getLabel().getId();

        String labelClass = labelRepo.findById(labelId).get().getName() ;
        List<String> labels = Arrays.asList(labelClass.split(","));
        Integer categories = labels.size();


        var allAnnotationsToIntegers =  LabelToNumberMapper.mapLabelsToNumbers(allAnnotationsT,categories);
        System.out.println(allAnnotationsToIntegers);

        List<Double> kappaValues = new ArrayList<>();

        for(List<List<Integer>> integers : allAnnotationsToIntegers){
            kappaValues.add(kappaEvaluationService.calculateKappa(integers , categories)) ;
        }

        Map<Long, Double> annotWithScor = new HashMap<>();
        for(int i = 0 ; i < annotatorIds.size() ; i++){
            annotWithScor.put(annotatorIds.get(i),kappaValues.get(i));
        }

//        return allAnnotationsToIntegers;
        return annotWithScor;

    }


}