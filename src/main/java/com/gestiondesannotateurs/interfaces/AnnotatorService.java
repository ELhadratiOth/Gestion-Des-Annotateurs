package com.gestiondesannotateurs.interfaces;

import com.gestiondesannotateurs.dtos.*;
import com.gestiondesannotateurs.entities.Annotator;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface AnnotatorService {
     Annotator getLastAnnotatorById();
     Annotator getAnnotatorById(Long annotatorId);
    Annotator getAnnotatorByEmail(String email);
    List<Annotator> getAllAnnotators();
    Annotator createAnnotator(AnnotatorDto annotator);
    Annotator updateAnnotator(Long annotatorId, AnnotatorDto annotator);
    void deleteAnnotator(Long annotatorId);
    public void markAsSpammer(Long id) ;
    public void deactivateAnnotator(Long id) ;
    public void activateAnnotator(Long id) ;
    List<Annotator> getAnnotatorSpamers(Long datasetId);
    List<AnnotatorTaskDto> getAnnotatorsByDataset(Long datasetId);
    Annotator getAnnotatorByTask(Long taskId);
    List<Annotator> getMatchingAnnotators (String name);
    Map<String, Map.Entry<Long, Long>> getAnnotatorsStats();
    List<AnnotatorTask> getListOfTasksForAnnotator(Long annotatorId);
    List<CoupleOfTextWithAnnotation> getCoupletextsWithAnnotationByAnnotator(Long annotatorId, Long taskId);
 }