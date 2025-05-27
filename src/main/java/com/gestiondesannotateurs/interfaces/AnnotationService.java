package com.gestiondesannotateurs.interfaces;

import com.gestiondesannotateurs.dtos.AnnotationDto;
import com.gestiondesannotateurs.dtos.AnnotationDtoAdmin;
import com.gestiondesannotateurs.dtos.AnnotationResponse;

import java.util.List;

public interface AnnotationService {
    AnnotationDto saveAnnotation(AnnotationDto annotationDto);
    List <AnnotationResponse> findByAnnotatorId(Long annotatorId);
    List<AnnotationResponse> findByAnnotatorIdAndCoupletextId(Long annotatorId,Long CoupleOfTextId);
    List<AnnotationResponse> getAnnotationsByDataset(Long datasetId);
    public long countAnnotationsForAnnotator(Long annotatorId);
    AnnotationDto findByAnnotationIdSharedWithAdmin(Long annotationId , Long coupleOfTextId);
    long getAnnotationsInLast24Hours();
    AnnotationDtoAdmin saveAnnotationAdmin(AnnotationDtoAdmin annotationDto);

}
