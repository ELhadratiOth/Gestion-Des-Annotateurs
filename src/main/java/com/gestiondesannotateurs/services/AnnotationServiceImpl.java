package com.gestiondesannotateurs.services;

import com.gestiondesannotateurs.dtos.AnnotationDto;
import com.gestiondesannotateurs.dtos.AnnotatorDto;
import com.gestiondesannotateurs.entities.AnnotationClass;
import com.gestiondesannotateurs.entities.Annotator;
import com.gestiondesannotateurs.entities.Coupletext;
import com.gestiondesannotateurs.entities.Dataset;
import com.gestiondesannotateurs.interfaces.AnnotationService;
import com.gestiondesannotateurs.repositories.AnnotationRepo;
import com.gestiondesannotateurs.repositories.AnnotatorRepo;
import com.gestiondesannotateurs.repositories.CoupleOfTextRepo;
import com.gestiondesannotateurs.repositories.DatasetRepo;
import com.gestiondesannotateurs.shared.Exceptions.AnnotatorNotFoundException;
import com.gestiondesannotateurs.shared.Exceptions.CustomResponseException;
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

    @Override
    public AnnotationDto saveAnnotation(AnnotationDto dto) {
        // Vérifier que l'annotateur existe
        Annotator annotator = annotatorRepo.findById(dto.getAnnotatorId())
                .orElseThrow(() -> new AnnotatorNotFoundException(dto.getAnnotatorId()));

        Coupletext coupletext = coupletextRepo.findById(dto.getCoupletextId())
                .orElseThrow(() -> new CustomResponseException(404,"Couple of ext Not Found"));

        AnnotationClass annotation = new AnnotationClass();
        annotation.setAnnotator(annotator);
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

}
