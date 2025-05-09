package com.gestiondesannotateurs.interfaces;

import com.gestiondesannotateurs.dtos.AnnotationDto;

import java.util.List;

public interface AnnotationService {
    AnnotationDto saveAnnotation(AnnotationDto annotationDto);
    List <AnnotationDto> findByAnnotatorId(Long annotatorId);
    List<AnnotationDto> findByAnnotatorIdAndCoupletextId(Long annotatorId,Long CoupleOfTextId);
    List<AnnotationDto> getAnnotationsByDataset(Long datasetId);
    public long countAnnotationsForAnnotator(Long annotatorId);
}
