package com.gestiondesannotateurs.interfaces;

import com.gestiondesannotateurs.dtos.AnnotationDto;
import com.gestiondesannotateurs.dtos.AnnotationDtoAdmin;
import com.gestiondesannotateurs.dtos.AnnotationResponse;

import java.util.List;

public interface AnnotationService {
    AnnotationDto saveAnnotation(AnnotationDto annotationDto);
    List <AnnotationResponse> findByAnnotatorIdAndTaskId(Long annotatorId,Long TaskId);
    List<AnnotationResponse> findByAnnotatorIdAndCoupletextId(Long annotatorId,Long CoupleOfTextId);
    List<AnnotationResponse> getAnnotationsByDataset(Long datasetId);
    public long countAnnotationsForAnnotatorByTask(Long annotatorId,Long taskId);
    AnnotationDto findByAnnotationIdSharedWithAdmin(Long annotationId , Long coupleOfTextId);
    long getAnnotationsInLast24Hours();
    AnnotationDtoAdmin saveAnnotationAdmin(AnnotationDtoAdmin annotationDto);

}
