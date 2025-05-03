 package com.gestiondesannotateurs.interfaces;

import com.gestiondesannotateurs.dtos.AnnotatorDto;
import com.gestiondesannotateurs.dtos.AnnotatorWithTaskId;
import com.gestiondesannotateurs.entities.Annotator;
import com.gestiondesannotateurs.entities.TaskToDo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

 @Service
public interface AnnotatorService {
    Annotator getAnnotatorById(Long annotatorId);
    List<Annotator> getAllAnnotators();
    Annotator createAnnotator(AnnotatorDto annotator);
    Annotator updateAnnotator(Long annotatorId, AnnotatorDto annotator);
    void deleteAnnotator(Long annotatorId);
    public void markAsSpammer(Long id) ;
    public void deactivateAnnotator(Long id) ;
    List<Annotator> getAnnotatorSpamers(Long datasetId);
    List<AnnotatorWithTaskId> getAnnotatorsByDataset(Long datasetId);

 }