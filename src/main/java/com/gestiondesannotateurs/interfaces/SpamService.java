package com.gestiondesannotateurs.interfaces;

import com.gestiondesannotateurs.entities.Annotator;
import java.util.List;
import java.util.Map;

public interface SpamService {
    Map<Long, Double> detectSpammer(Long datasetId);
    List<Annotator> getAllSpammers();
}