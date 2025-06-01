package com.gestiondesannotateurs.interfaces;

import java.util.List;
import java.util.Map;

public interface KappaEvaluationService {
    double calculateKappa(List<List<Integer>> annotations, int numberOfCategories);
    String getMostFrequentCategoryWithKappa(List<Integer> singleItemAnnotations,
                                          Map<Integer, String> categoryLabels);


//    public double calculatePE(List<List<Integer>> annotations, int numberOfCategories);
}