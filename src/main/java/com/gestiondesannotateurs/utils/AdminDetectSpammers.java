package com.gestiondesannotateurs.utils;

import com.gestiondesannotateurs.dtos.AnnotationDto;
import com.gestiondesannotateurs.dtos.CoupleOfTextWithAnnotation;
import com.gestiondesannotateurs.dtos.TaskToDoDto;
import com.gestiondesannotateurs.entities.Label;
import com.gestiondesannotateurs.interfaces.*;
import com.gestiondesannotateurs.repositories.DatasetRepo;
import com.gestiondesannotateurs.repositories.LabelRepo;
import com.gestiondesannotateurs.services.KappaEvaluationServiceImpl;
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
    private KappaEvaluationServiceImpl kappaEvaluationService;




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
//        System.out.println("nmbr of  annotators : "+annotatorIds.size());

        // 3. Collect annotations

        Set<Long>  isFinishAnnotation = new HashSet<>();

        List<List<String>> allAnnotations = new ArrayList<>();
        for (CoupleOfTextWithAnnotation coupleOfText : coupleOfTextAnnotated) {
            List<String> annotationForCoupleOfText = new ArrayList<>();
            annotationForCoupleOfText.add(coupleOfText.annotationLabel());
            for (Long annotatorId : annotatorIds) {

                AnnotationDto annotationDto = annotationService.findByAnnotationIdSharedWithAdmin(annotatorId, coupleOfText.coupleOfTextId());
                String annot = annotationDto != null &&   annotationDto.getLabel() != null ? annotationDto.getLabel() : "not annotated";
                if (annot.equals("not annotated")){
                    isFinishAnnotation.add(annotatorId);
                }

                annotationForCoupleOfText.add(annot);
            }
            allAnnotations.add(annotationForCoupleOfText);
        }
        System.out.println("labels annotat :");

        System.out.println(allAnnotations);
        int  counter = 0 ;
        for(Long annotatorNotfinished : isFinishAnnotation ){

            int pos = annotatorIds.indexOf(annotatorNotfinished);
            for( List<String> annotsList : allAnnotations){
                annotsList.remove(pos - counter);
            }
            counter--;
            System.out.println("the  position is  descreased by :" + counter);
        }


        var allAnnotationsT=  AnnotationTransformer.transformAnnotations(allAnnotations);
        System.out.println("hundelr");

        System.out.println(allAnnotationsT);
        Long labelId = datasetService.findDatasetById(datasetId).getLabel().getId();

        String labelClass = labelRepo.findById(labelId).get().getClasses() ;
        System.out.println(labelClass);
        List<String> labels = Arrays.asList(labelClass.split(";"));
        Integer categories = labels.size();
        System.out.println("categories : "+categories);




        var allAnnotationsToIntegers =  LabelToNumberMapper.mapLabelsToNumbers(allAnnotationsT,categories);
        System.out.println(allAnnotationsToIntegers);

        List<Double> kappaValues = new ArrayList<>();

        for(List<List<Integer>> integers : allAnnotationsToIntegers){
            System.out.println("les results des annotateurs : ");

            System.out.println(integers);
            kappaValues.add(kappaEvaluationService.calculatePE(integers , categories)) ;
        }
        System.out.println("agremement: ");

        System.out.println(kappaValues);

        Map<Long, Double> annotWithScor = new HashMap<>();
        for(int i = 0 ; i < annotatorIds.size() ; i++){
            if(isFinishAnnotation.contains(annotatorIds.get(i)) ){
                annotWithScor.put(annotatorIds.get(i),0.0);

            }
            else{
                annotWithScor.put(annotatorIds.get(i),kappaValues.get(i));

            }
        }

//        return allAnnotationsToIntegers;
        System.out.println("hhheleelle");

        System.out.println(annotWithScor);
        return annotWithScor;

    }


}