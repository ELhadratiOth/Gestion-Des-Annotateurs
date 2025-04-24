package com.gestiondesannotateurs.interfaces;

import com.gestiondesannotateurs.entities.Annotator;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface AnnotatorService {
    Annotator getAnnotator(Long annotatorId);
    List<Annotator> getAllAnnotators();
    Annotator createAnnotator(Annotator annotator);
    Annotator updateAnnotator(Long annotatorId, Annotator annotator);
    void deleteAnnotator(Long annotatorId);
}