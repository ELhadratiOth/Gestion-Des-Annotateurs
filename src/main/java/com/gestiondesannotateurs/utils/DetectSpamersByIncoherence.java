package com.gestiondesannotateurs.utils;

import com.gestiondesannotateurs.dtos.AnnotatorTaskDto;
import com.gestiondesannotateurs.entities.AnnotationClass;
import com.gestiondesannotateurs.entities.Annotator;
import com.gestiondesannotateurs.interfaces.AnnotationService;
import com.gestiondesannotateurs.interfaces.AnnotatorService;
import com.gestiondesannotateurs.repositories.AnnotationRepo;
import com.gestiondesannotateurs.repositories.AnnotatorRepo;
import com.gestiondesannotateurs.shared.Exceptions.AnnotatorNotFoundException;
import com.gestiondesannotateurs.shared.Exceptions.CustomResponseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class DetectSpamersByIncoherence {

    private static final Logger logger = LoggerFactory.getLogger(DetectSpamersByIncoherence.class);

    @Autowired
    private AnnotatorService annotatorService;

    @Autowired
    private AnnotationService annotationService;

    @Autowired
    private AnnotationRepo annotationRepo;

    @Autowired
    private AnnotatorRepo annotatorRepo;

    // 👉 configurable threshold
    private static final int INCOHERENCE_THRESHOLD = 4;

    public double detectSpammerByInconsistency(Long annotatorId) {
        Annotator annotator = annotatorService.getAnnotatorById(annotatorId);
        if (annotator == null) {
            throw new AnnotatorNotFoundException(annotatorId);
        }

        List<AnnotationClass> annotations = annotationRepo.findByAnnotatorId(annotatorId);
        if (annotations.isEmpty()) {
            logger.info("Annotator {} has no annotations. Skipping.", annotatorId);
        }

        // Regroup by coupletext ID and collect all labels
        Map<Long, Set<String>> coupleToLabelSet = annotations.stream()
                .collect(Collectors.groupingBy(
                        a -> a.getCoupletext().getId(),
                        Collectors.mapping(AnnotationClass::getChoosenLabel, Collectors.toSet())
                ));

        // Count how many couples have inconsistent labels
        long inconsistentCount = coupleToLabelSet.values().stream()
                .filter(labelSet -> labelSet.size() > 1) // incohérence = plusieurs labels différents
                .count();

        boolean isSpammer = inconsistentCount > INCOHERENCE_THRESHOLD;
        annotator.setSpammer(isSpammer);
        annotatorRepo.save(annotator);

        logger.info("Annotator {} → Inconsistent pairs: {}, Spammer: {}",
                annotatorId, inconsistentCount, isSpammer);
        return inconsistentCount;
    }

    // ✅ Execution automatique toutes les heures
    public Map<Long, Double> detectAllSpammersByInconsistency(Long datasetId) {

        List<AnnotatorTaskDto> allAnnotators = annotatorService.getAnnotatorsByDataset(datasetId);
        Map<Long, Double> inconsistencies = new HashMap<>();

        for (AnnotatorTaskDto a : allAnnotators) {
            try {
                double inconsistency = detectSpammerByInconsistency(a.getAnnotatorId());
                inconsistencies.put(a.getAnnotatorId(), inconsistency);
            } catch (Exception e) {
                new CustomResponseException(404,"error processing annotator "+a.getAnnotatorId());
            }
        }

        return inconsistencies;
    }

//    @Scheduled(cron = "0 0 * * * *")
//    public void detectAutomatically(){
//        logger.info("Running scheduled spammer detection...");
//        List<Annotator> allAnnotators = annotatorService.getAllAnnotators();
//        Map<Long, Double> inconsistencies = new HashMap<>();
//
//        for (Annotator a : allAnnotators) {
//            try {
//                double inconsistency = detectSpammerByInconsistency(a.getId());
//                inconsistencies.put(a.getId(), inconsistency);
//            } catch (Exception e) {
//                logger.error("Error processing annotator {}: {}", a.getId(), e.getMessage());
//            }
//        }
//
//        logger.info("Spammer detection completed.");
//    }



}
