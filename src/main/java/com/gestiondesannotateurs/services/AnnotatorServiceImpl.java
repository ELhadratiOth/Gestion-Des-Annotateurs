package com.gestiondesannotateurs.services;
import com.gestiondesannotateurs.interfaces.AnnotatorService;
import com.gestiondesannotateurs.shared.Exceptions.AnnotatorNotFoundException;
import com.gestiondesannotateurs.entities.Annotator;
import com.gestiondesannotateurs.repositories.AnnotatorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AnnotatorServiceImpl implements AnnotatorService {
    @Autowired
    private final AnnotatorRepository annotatorRepository;

    public AnnotatorServiceImpl(AnnotatorRepository annotatorRepository) {
        this.annotatorRepository = annotatorRepository;
    }

    @Override
    public Annotator getAnnotator(Long annotatorId) {
        return annotatorRepository.findById(annotatorId)
                .orElseThrow(() -> new AnnotatorNotFoundException("Annotateur non trouvé avec l'ID: " + annotatorId));
    }

    @Override
    public List<Annotator> getAllAnnotators() {
        return annotatorRepository.findAll();
    }

    @Override
    public Annotator createAnnotator(Annotator annotator) {
        return annotatorRepository.save(annotator);
    }

    @Override
    public Annotator updateAnnotator(Long annotatorId, Annotator annotator) {
        if (!annotatorRepository.existsById(annotatorId)) {
            throw new AnnotatorNotFoundException("Annotateur non trouvé avec l'ID: " + annotatorId);
        }
        annotator.setId(annotatorId);
        return annotatorRepository.save(annotator);
    }

    @Override
    public void deleteAnnotator(Long annotatorId) {
        if (!annotatorRepository.existsById(annotatorId)) {
            throw new AnnotatorNotFoundException("Annotateur non trouvé avec l'ID: " + annotatorId);
        }
        annotatorRepository.deleteById(annotatorId);
    }
}