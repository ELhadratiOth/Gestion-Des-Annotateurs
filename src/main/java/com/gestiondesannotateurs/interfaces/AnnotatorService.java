 package com.gestiondesannotateurs.interfaces;

import com.gestiondesannotateurs.dtos.AnnotatorDto;
import com.gestiondesannotateurs.entities.Annotator;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface AnnotatorService {
    Annotator getAnnotatorById(Long annotatorId);
    List<Annotator> getAllAnnotators();
    Annotator createAnnotator(AnnotatorDto annotator);
    Annotator updateAnnotator(Long annotatorId, AnnotatorDto annotator);
    void deleteAnnotator(Long annotatorId);
    public void markAsSpammer(Long id) ;
    public void deactivateAnnotator(Long id) ; 
}