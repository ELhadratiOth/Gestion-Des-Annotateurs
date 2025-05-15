package com.gestiondesannotateurs.utils;

import com.gestiondesannotateurs.dtos.AnnotationDto;
import com.gestiondesannotateurs.entities.AnnotationClass;
import com.gestiondesannotateurs.entities.Annotator;
import com.gestiondesannotateurs.interfaces.AnnotationService;
import com.gestiondesannotateurs.interfaces.AnnotatorService;
import com.gestiondesannotateurs.repositories.AnnotationRepo;
import com.gestiondesannotateurs.repositories.AnnotatorRepo;
import com.gestiondesannotateurs.shared.Exceptions.AnnotatorNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class DetectSpamersByIncoherence {

    @Autowired
    private AnnotatorService annotatorService;

    @Autowired
    private AnnotationService annotationService;
    @Autowired
    private AnnotationRepo annotationRepo;
    @Autowired
    private AnnotatorRepo annotatorRepo;

    public void detectSpammerByInconsistency(Long annotatorId) {
        Annotator annotator = annotatorService.getAnnotatorById(annotatorId);
        if (annotator == null) {
            throw new AnnotatorNotFoundException(annotatorId);
        }


        List<AnnotationClass> annotations = annotationRepo.findByAnnotatorId(annotatorId);

        Map<Long, List<String>> coupleToLabels = annotations.stream()
                .collect(Collectors.groupingBy(
                        a -> a.getCoupletext().getId(),
                        Collectors.mapping(AnnotationClass::getChoosenLabel, Collectors.toList())
                ));

        long inconsistentCount = coupleToLabels.values().stream()
                .filter(labels -> labels.size() >= 2 && new HashSet<>(labels).size() > 1)
                .count();

        // Set annotator spammer is inconsistence > repetitionsPerAnnotator//2 +1 can customize
        annotator.setSpammer(inconsistentCount > 3);
        annotatorRepo.save(annotator);
    }

    @Scheduled(cron = "0 0 * * * *")
    public void detectAllSpammersByInconsistency() {
        annotatorRepo.findAll()
                .forEach(a -> detectSpammerByInconsistency(a.getId()));
    }
}
