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

    @Autowired
    private AdminDetectSpammers adminDetectSpammers;

    @Autowired
    private DetectSpamersByIncoherence detectSpamersByIncoherence;

    @Autowired
    private DatasetRepo datasetRepo;

    @Autowired
    private AnnotatorRepo annotatorRepo;

    public Map<Long, Double> detectSpammer(Long datasetId) {

        final Double TRESHOLD = 0.3;

        Dataset dataset = datasetRepo.findById(datasetId).get();
        if(dataset==null){
            throw new CustomResponseException(404,"No dataset found with this id");
        }
        //1 admin check
        Map<Long, Double>  annotatorsAnnotation = adminDetectSpammers.detect(datasetId);

        Map<Long, Double> inconsistencies=detectSpamersByIncoherence.detectAllSpammersByInconsistency(datasetId);
        Map<Long, Double> averagedScores = new HashMap<>();

        // compute the averge spamscore
        for (Map.Entry<Long, Double> entry : annotatorsAnnotation.entrySet()) {
            Long annotatorId = entry.getKey();
            Double score1 = entry.getValue();
            Double score2 = inconsistencies.get(annotatorId);

            if (score2 != null) { // S'assurer que l'annotateur existe aussi dans la 2e map
                double average = (score1 + score2) / 2.0;
                if (average<=TRESHOLD) {
                    Annotator curAnn= annotatorRepo.findById(annotatorId).get();
                    curAnn.setSpammer(true);
                    annotatorRepo.save(curAnn);
                }
                averagedScores.put(annotatorId, average);
            } else {
                throw new CustomResponseException(404, "Error one annotator not in the two map");

            }

        }
        return inconsistencies;

    }


}
