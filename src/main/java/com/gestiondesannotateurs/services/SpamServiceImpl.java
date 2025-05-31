package com.gestiondesannotateurs.services;

import com.gestiondesannotateurs.entities.Annotator;
import com.gestiondesannotateurs.entities.Dataset;
import com.gestiondesannotateurs.interfaces.SpamService;
import com.gestiondesannotateurs.repositories.AnnotatorRepo;
import com.gestiondesannotateurs.repositories.DatasetRepo;
import com.gestiondesannotateurs.shared.Exceptions.CustomResponseException;
import com.gestiondesannotateurs.shared.Exceptions.GlobalSuccessHandler;
import com.gestiondesannotateurs.utils.AdminDetectSpammers;
import com.gestiondesannotateurs.utils.DetectSpamersByIncoherence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class SpamServiceImpl implements SpamService {
//
//    @Autowired
//    private AdminDetectSpammers adminDetectSpammers;

    @Autowired
    private DetectSpamersByIncoherence detectSpamersByIncoherence;

    @Autowired
    private DatasetRepo datasetRepo;

    @Autowired
    private AnnotatorRepo annotatorRepo;

    public Map<Long, Double> detectSpammer(Long datasetId) {

        final Double TRESHOLD = 0.3;

        Dataset dataset = datasetRepo.findById(datasetId)
                .orElseThrow(() -> new CustomResponseException(404, "No dataset found with this id"));

        // 1. Scores : admin (high score  = good)
//        Map<Long, Double> annotatorsAnnotation = adminDetectSpammers.detect(datasetId);
        // 2. Scores : incoherences (high score  = bad)
        Map<Long, Double> inconsistencies = detectSpamersByIncoherence.detectAllSpammersByInconsistency(datasetId);

//        Map<Long, Double> finalSpammerScores = new HashMap<>();
//
//        for (Map.Entry<Long, Double> entry : annotatorsAnnotation.entrySet()) {
//            Long annotatorId = entry.getKey();
//            Double adminAgreementScore = entry.getValue();
//            Double incoherenceScore = inconsistencies.get(annotatorId);
//
//            if (incoherenceScore != null) {
//                // Normalisation
//                double adminDisagreeScore = 1.0 - adminAgreementScore;
//
//                // ponderation ti give more importance to desagrement with admin :
//                double finalScore = 0.7 * adminDisagreeScore + 0.3 * incoherenceScore;
//
//                if (finalScore >= TRESHOLD) {
//                    Annotator curAnn = annotatorRepo.findById(annotatorId)
//                            .orElseThrow(() -> new CustomResponseException(404, "Annotator not found"));
//                    curAnn.setSpammer(true);
//                    annotatorRepo.save(curAnn);
//                }
//
//                finalSpammerScores.put(annotatorId, finalScore);
//            } else {
//                throw new CustomResponseException(404, "Annotator not found in both maps: " + annotatorId);
//            }
//        }

        return inconsistencies;
    }


}
