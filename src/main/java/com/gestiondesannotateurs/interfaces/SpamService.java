package com.gestiondesannotateurs.interfaces;

import com.gestiondesannotateurs.utils.AdminDetectSpammers;
import com.gestiondesannotateurs.utils.DetectSpamersByIncoherence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface SpamService {
    Map<Long, Double> detectSpammer(Long datasetId);
}
