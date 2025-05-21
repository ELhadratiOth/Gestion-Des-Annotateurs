package com.gestiondesannotateurs.utils;

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

    public void detectSpammerByInconsistency(Long annotatorId) {
        Annotator annotator = annotatorService.getAnnotatorById(annotatorId);
        if (annotator == null) {
            throw new AnnotatorNotFoundException(annotatorId);
        }

        List<AnnotationClass> annotations = annotationRepo.findByAnnotatorId(annotatorId);
        if (annotations.isEmpty()) {
            logger.info("Annotator {} has no annotations. Skipping.", annotatorId);
            return;
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
    }

    // ✅ Execution automatique toutes les heures
    @Scheduled(cron = "0 0 * * * *")
    public void detectAllSpammersByInconsistency() {
        logger.info("Running scheduled spammer detection...");

        List<Annotator> allAnnotators = annotatorRepo.findAll();
        allAnnotators.forEach(a -> {
            try {
                detectSpammerByInconsistency(a.getId());
            } catch (Exception e) {
                logger.error("Error processing annotator {}: {}", a.getId(), e.getMessage());
            }
        });

        logger.info("Spammer detection completed.");
    }
}
