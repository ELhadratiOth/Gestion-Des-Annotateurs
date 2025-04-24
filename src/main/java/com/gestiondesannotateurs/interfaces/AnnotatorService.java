package com.gestiondesannotateurs.services;

import com.gestiondesannotateurs.entities.Annotator;
import java.util.List;

public interface AnnotatorService {
    Annotator getAnnotator(Long annotatorId);
    List<Annotator> getAllAnnotators();
    Annotator createAnnotator(Annotator annotator);
    Annotator updateAnnotator(Long annotatorId, Annotator annotator);
    void deleteAnnotator(Long annotatorId);
}