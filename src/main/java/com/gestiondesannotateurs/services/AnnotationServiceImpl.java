package com.gestiondesannotateurs.services;

import com.gestiondesannotateurs.dtos.AnnotationDto;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    @Override
    public AnnotationDto saveAnnotation(AnnotationDto dto) {
        // Validate input
        if (dto == null || dto.getAnnotatorId() == null || dto.getCoupletextId() == null) {
            throw new CustomResponseException(400, "Invalid input: Annotator ID or Coupletext ID is missing");
        }

        // Check if the annotatorId corresponds to an Annotator
        Optional<Annotator> annotator = annotatorRepo.findById(dto.getAnnotatorId());
        if (annotator.isPresent()) {
            // Coupletext lookup
            Coupletext coupletext = coupletextRepo.findById(dto.getCoupletextId())
                    .orElseThrow(() -> new CustomResponseException(404, "Coupletext Not Found"));

            // Save annotation
            AnnotationClass annotation = new AnnotationClass();
            annotation.setAnnotator(annotator.get());
            annotation.setCoupletext(coupletext);
            annotation.setChoosenLabel(dto.getLabel());
            annotationRepo.save(annotation);
            Long datasetId = annotation.getCoupletext().getDataset().getId();
            datasetService.updateDatasetAdvancement(datasetId);
            return dto; // Consider returning a DTO with the saved annotation's ID
        }

        // If not an Annotator, check if it's an Admin
        Optional<Admin> admin = adminRepo.findById(dto.getAnnotatorId());
        if (admin.isEmpty()) {
            throw new CustomResponseException(404, "User Not Found: Neither Annotator nor Admin");
        }

        // Coupletext lookup
        Coupletext coupletext = coupletextRepo.findById(dto.getCoupletextId())
                .orElseThrow(() -> new CustomResponseException(404, "Coupletext Not Found"));

        // Save annotation
        AnnotationClass annotation = new AnnotationClass();
        annotation.setAnnotator(admin.get());
        annotation.setIsAdmin(true);
        annotation.setCoupletext(coupletext);
        annotation.setChoosenLabel(dto.getLabel());
        annotationRepo.save(annotation);
        return dto;
    }

    @Override
    public List <AnnotationDto> findByAnnotatorId(Long annotatorId){
        Optional<Annotator> annotator= Optional.ofNullable(annotatorRepo.findById(annotatorId)
                .orElseThrow(() -> new AnnotatorNotFoundException(annotatorId)));

        List <AnnotationClass> annotationClasses = annotationRepo.findByAnnotatorId(annotatorId);
        if(annotationClasses.isEmpty()){
            new CustomResponseException(404,"Annotator don't have annotations");
        }
        List <AnnotationDto> result=new ArrayList<>();
        for(AnnotationClass annotationClasse:annotationClasses){
            AnnotationDto currAnnot= new  AnnotationDto();
            currAnnot.setAnnotatorId(annotatorId);
            currAnnot.setLabel(annotationClasse.getChoosenLabel());
            currAnnot.setCoupletextId(annotationClasse.getCoupletext().getId());
            result.add(currAnnot);
        }

        return result;

    }
    @Override
    public List<AnnotationDto> findByAnnotatorIdAndCoupletextId(Long annotatorId,Long coupleOfTextId){
        Optional<Annotator> annotator= Optional.ofNullable(annotatorRepo.findById(annotatorId)
                .orElseThrow(() -> new AnnotatorNotFoundException(annotatorId)));
        Optional<Coupletext> coupletext= Optional.ofNullable(coupletextRepo.findById(annotatorId)
                .orElseThrow(() -> new CustomResponseException(404,"No such Couple of text")));
        List <AnnotationClass> annotationClasses = annotationRepo.findByAnnotatorIdAndCoupletextId(annotatorId,coupleOfTextId);
        if(annotationClasses.isEmpty()){
            new CustomResponseException(404,"Annotator don't have annotations for this Couple of text");
        }
        List <AnnotationDto> result=new ArrayList<>();
        for(AnnotationClass annotationClasse:annotationClasses){
            AnnotationDto currAnnot= new  AnnotationDto();
            currAnnot.setAnnotatorId(annotatorId);
            currAnnot.setLabel(annotationClasse.getChoosenLabel());
            currAnnot.setCoupletextId(annotationClasse.getCoupletext().getId());
            result.add(currAnnot);
        }

        return result;

    }
    @Override
    public List<AnnotationDto> getAnnotationsByDataset(Long datasetId){
        Optional<Dataset> dt= Optional.ofNullable(datasetRepo.findById(datasetId)
                .orElseThrow(() -> new CustomResponseException(404,"Dataset don't exist")));
        List<AnnotationClass> annotationClasses=annotationRepo.findByDatasetId(dt.get().getId());
        if(annotationClasses.isEmpty()){
            new CustomResponseException(404,"This Dataset wasn't yet annotated");
        }
        List <AnnotationDto> result=new ArrayList<>();
        for(AnnotationClass annotationClasse:annotationClasses){
            AnnotationDto currAnnot= new  AnnotationDto();
            currAnnot.setAnnotatorId(annotationClasse.getAnnotator().getId());
            currAnnot.setLabel(annotationClasse.getChoosenLabel());
            currAnnot.setCoupletextId(annotationClasse.getCoupletext().getId());
            result.add(currAnnot);
        }

        return result;

    }
    @Override
    public long countAnnotationsForAnnotator(Long annotatorId) {
        Optional<Annotator> annotator= Optional.ofNullable(annotatorRepo.findById(annotatorId)
                .orElseThrow(() -> new AnnotatorNotFoundException(annotatorId)));
        return findByAnnotatorId(annotatorId).toArray().length;
    }

    @Override
    public AnnotationDto findByAnnotationIdSharedWithAdmin(Long annotatorId, Long coupleOfTextId) {
        // Check if annotator exists
        Optional<Annotator> annotator = annotatorRepo.findById(annotatorId);
//        System.out.println(annotator.get().getId() + "!!!! " + annotatorId);
        if (annotator.isEmpty()) {
            throw new CustomResponseException(404,"Annotator with ID " + annotatorId + " does not exist");
        }

        // Query annotation by annotatorId
        Optional<AnnotationClass> annotationClasse = annotationRepo.findByAnnotatorIdSharedWithAdmin(annotatorId,coupleOfTextId );
//        System.out.println(annotationClasse.get().getCoupletext().getId() + "!!!! " + coupleOfTextId);
        if (annotationClasse.isEmpty()) {
            throw new CustomResponseException(404,"Annotator " + annotator.get().getFirstName() + " has not yet annotate the couple of text shared with admin");
        }

        AnnotationClass annotation = annotationClasse.get();
        AnnotationDto annotationDto = new AnnotationDto();
        annotationDto.setAnnotatorId(annotatorId);
        annotationDto.setLabel(annotation.getChoosenLabel());
        annotationDto.setCoupletextId(annotation.getCoupletext().getId());

        return annotationDto;
    }

}
