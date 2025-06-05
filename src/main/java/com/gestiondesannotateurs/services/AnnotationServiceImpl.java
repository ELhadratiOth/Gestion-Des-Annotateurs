package com.gestiondesannotateurs.services;

import com.gestiondesannotateurs.dtos.AnnotationDto;
import com.gestiondesannotateurs.dtos.AnnotationDtoAdmin;
import com.gestiondesannotateurs.dtos.AnnotationResponse;
import com.gestiondesannotateurs.dtos.AnnotatorDto;
import com.gestiondesannotateurs.entities.*;
import com.gestiondesannotateurs.interfaces.AnnotationService;
import com.gestiondesannotateurs.interfaces.DatasetService;
import com.gestiondesannotateurs.repositories.*;
import com.gestiondesannotateurs.shared.Exceptions.AnnotatorNotFoundException;
import com.gestiondesannotateurs.shared.Exceptions.CustomResponseException;
import com.gestiondesannotateurs.shared.Exceptions.GlobalSuccessHandler;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class AnnotationServiceImpl implements AnnotationService {
    @Autowired
    private AnnotationRepo annotationRepo;

    @Autowired
    private AnnotatorRepo annotatorRepo;

    @Autowired
    private CoupleOfTextRepo coupletextRepo;

    @Autowired
    private DatasetRepo datasetRepo;
    @Autowired
    private AdminRepo adminRepo;

    @Autowired
    private DatasetService datasetService;
    @Autowired
    private OthmanRepo othmanRepo;
    @Autowired
    private PersonRepo personRepo;

    @Autowired
    private TaskToDoRepo taskToDoRepo;

    @Override
    public AnnotationDto saveAnnotation(AnnotationDto dto) {
        // Validate input
        if (dto == null || dto.getAnnotatorId() == null || dto.getCoupletextId() == null) {
            throw new CustomResponseException(400, "Invalid input: Annotator ID or Coupletext ID is missing");
        }

        // Check if the annotatorId corresponds to an Annotator
        Optional<Person> person = personRepo.findById(dto.getAnnotatorId());

        if (person.isEmpty()) {
            throw new CustomResponseException(404, "User Not Found: Neither Annotator nor Admin nor  super admin");
        }

        // Coupletext lookup
        Coupletext coupletext = coupletextRepo.findById(dto.getCoupletextId())
                .orElseThrow(() -> new CustomResponseException(404, "Coupletext Not Found"));

        // Save annotation
        AnnotationClass annotation = new AnnotationClass();
        annotation.setAnnotator(person.get());
        annotation.setCoupletext(coupletext);
        annotation.setChoosenLabel(dto.getLabel());
        annotationRepo.save(annotation);
        //After save the annotation update the related datatasets and task progress
        Long datasetId = annotation.getCoupletext().getDataset().getId();
        datasetService.updateDatasetAdvancement(datasetId);
        return dto;


    }

    @Override
    public List<AnnotationResponse> findByAnnotatorIdAndTaskId(Long annotatorId, Long taskId) {
        // 1. Vérifier que l’annotateur existe
        Annotator annotator = annotatorRepo.findById(annotatorId)
                .orElseThrow(() -> new AnnotatorNotFoundException(annotatorId));

        // 2. Vérifier que la tâche existe
        TaskToDo task = taskToDoRepo.findById(taskId)
                .orElseThrow(() -> new CustomResponseException(404, "Task not found"));

        // 3. Récupérer les IDs des Coupletexts associés à la tâche
        Set<Long> coupleIdsInTask = task.getCoupletexts().stream()
                .map(Coupletext::getId)
                .collect(Collectors.toSet());

        // 4. Récupérer toutes les annotations faites par cet annotateur
        List<AnnotationClass> annotationClasses = annotationRepo.findByAnnotatorId(annotatorId);

        if (annotationClasses.isEmpty()) {
             new CustomResponseException(404, "Annotator doesn't have annotations");
        }

        // 5. Filtrer uniquement les annotations dont le coupletext est lié à cette tâche
        List<AnnotationResponse> result = new ArrayList<>();
        for (AnnotationClass annotation : annotationClasses) {
            Coupletext couple = annotation.getCoupletext();
            if (couple != null && coupleIdsInTask.contains(couple.getId())) {
                AnnotationResponse currAnnot = new AnnotationResponse();
                currAnnot.setAnnotatorId(annotatorId);
                currAnnot.setLabel(annotation.getChoosenLabel());
                currAnnot.setCoupletextId(couple.getId());
                currAnnot.setTextA(couple.getTextA());
                currAnnot.setTextB(couple.getTextB());
                result.add(currAnnot);
            }
        }

        return result;
    }


    @Override
    public List<AnnotationResponse> findByAnnotatorIdAndCoupletextId(Long annotatorId,Long coupleOfTextId){
        Optional<Annotator> annotator= Optional.ofNullable(annotatorRepo.findById(annotatorId)
                .orElseThrow(() -> new AnnotatorNotFoundException(annotatorId)));
        Optional<Coupletext> coupletext= Optional.ofNullable(coupletextRepo.findById(annotatorId)
                .orElseThrow(() -> new CustomResponseException(404,"No such Couple of text")));
        List <AnnotationClass> annotationClasses = annotationRepo.findByAnnotatorIdAndCoupletextId(annotatorId,coupleOfTextId);
        if(annotationClasses.isEmpty()){
            new CustomResponseException(404,"Annotator don't have annotations for this Couple of text");
        }
        List <AnnotationResponse> result=new ArrayList<>();
        for(AnnotationClass annotationClasse:annotationClasses){
            AnnotationResponse currAnnot= new  AnnotationResponse();
            currAnnot.setAnnotatorId(annotatorId);
            currAnnot.setLabel(annotationClasse.getChoosenLabel());
            currAnnot.setCoupletextId(annotationClasse.getCoupletext().getId());
            currAnnot.setTextA(annotationClasse.getCoupletext().getTextA());
            currAnnot.setTextB(annotationClasse.getCoupletext().getTextB());
            result.add(currAnnot);
        }

        return result;

    }
    @Override
    public List<AnnotationResponse> getAnnotationsByDataset(Long datasetId){
        Optional<Dataset> dt= Optional.ofNullable(datasetRepo.findById(datasetId)
                .orElseThrow(() -> new CustomResponseException(404,"Dataset don't exist")));
        List<AnnotationClass> annotationClasses=annotationRepo.findByDatasetId(dt.get().getId());
        if(annotationClasses.isEmpty()){
            new CustomResponseException(404,"This Dataset wasn't yet annotated");
        }
        List <AnnotationResponse> result=new ArrayList<>();
        for(AnnotationClass annotationClasse:annotationClasses){
            AnnotationResponse currAnnot= new AnnotationResponse();
            currAnnot.setAnnotatorId(annotationClasse.getAnnotator().getId());
            currAnnot.setLabel(annotationClasse.getChoosenLabel());
            currAnnot.setCoupletextId(annotationClasse.getCoupletext().getId());
            currAnnot.setTextA(annotationClasse.getCoupletext().getTextA());
            currAnnot.setTextB(annotationClasse.getCoupletext().getTextB());
            result.add(currAnnot);
        }

        return result;

    }
    @Override
    public long countAnnotationsForAnnotatorByTask(Long annotatorId,Long taskId) {
        Optional<Annotator> annotator= Optional.ofNullable(annotatorRepo.findById(annotatorId)
                .orElseThrow(() -> new AnnotatorNotFoundException(annotatorId)));
        return findByAnnotatorIdAndTaskId(annotatorId,taskId).toArray().length;
    }

    @Override
    public AnnotationDto findByAnnotationIdSharedWithAdmin(Long annotatorId, Long coupleOfTextId) {
        // Check if annotator exists
        Optional<Annotator> annotator = annotatorRepo.findById(annotatorId);
        System.out.println(annotator.get().getId() + "!!!! " + annotatorId);
        if (annotator.isEmpty()) {
            throw new CustomResponseException(404,"Annotator with ID " + annotatorId + " does not exist");
        }

        // Query annotation by annotatorId
        List<AnnotationClass> annotationClasse = annotationRepo.findByAnnotatorIdSharedWithAdmin(annotatorId,coupleOfTextId );
        if (annotationClasse.isEmpty()) {
//            throw new CustomResponseException(404,"Annotator " + annotator.get().getFirstName() + " has not yet annotate the couple of text shared with admin");
            return null;
        }
        System.out.println(annotationClasse.get(0).getCoupletext().getId() + " ===== " + coupleOfTextId);


        AnnotationClass annotation = annotationClasse.get(0);
        AnnotationDto annotationDto = new AnnotationDto();
        annotationDto.setAnnotatorId(annotatorId);
        annotationDto.setLabel(annotation.getChoosenLabel());
        annotationDto.setCoupletextId(annotation.getCoupletext().getId());

        return annotationDto;
    }


    public long getAnnotationsInLast24Hours() {
        LocalDateTime twentyFourHoursAgo = LocalDateTime.now().minusHours(24);
        return annotationRepo.countAnnotationsInLast24Hours(twentyFourHoursAgo);
    }

    @Override
    public AnnotationDtoAdmin saveAnnotationAdmin(AnnotationDtoAdmin dto) {
        // Validate input
        if (dto == null || dto.getAnnotatorId() == null || dto.getCoupletextId() == null) {
            throw new CustomResponseException(400, "Invalid input: Annotator ID or Coupletext ID is missing");
        }

        // Check if the annotatorId corresponds to an Annotator
        Optional<Person> person = personRepo.findById(dto.getAnnotatorId());

        if (person.isEmpty()) {
            throw new CustomResponseException(404, "User Not Found: Neither Annotator nor Admin nor  super admin");
        }

        // Coupletext lookup
        Coupletext coupletext = coupletextRepo.findById(dto.getCoupletextId())
                .orElseThrow(() -> new CustomResponseException(404, "Coupletext Not Found"));

        Optional<AnnotationClass> annotExists = annotationRepo.findIfAlreadyAnnotatedByAdmin(coupletext.getId());
        if( annotExists.isPresent() ){
            annotExists.get().setChoosenLabel( dto.getLabel());
            annotExists.get().setCreatedAt( LocalDateTime.now());
        }
        else{
            // Save annotation
            AnnotationClass annotation = new AnnotationClass();
            annotation.setAnnotator(person.get());
            annotation.setCoupletext(coupletext);
            annotation.setChoosenLabel(dto.getLabel());
            annotation.setIsAdmin(dto.getIsAdmin());
            annotationRepo.save(annotation);
//            Long datasetId = annotation.getCoupletext().getDataset().getId();
        }


        return dto;
    }

}
