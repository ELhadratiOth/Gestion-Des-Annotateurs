package com.gestiondesannotateurs.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LabelToNumberMapper {

    /**
     * Transforms a list of paired annotation labels from couple-based to annotator-index-based grouping
     * and maps labels to numerical values based on the number of categories.
     * Input format: [[[annotator_label, admin_label], ...], ...] (grouped by couple of text)
     * Output format: [[[annotator_number, admin_number], ...], ...] (grouped by annotator index)
     * Maps the first 'categoryCount' unique labels to 0 to categoryCount-1, additional labels to subsequent integers.
     *
     * @param annotations   List of paired annotation labels
     * @param categoryCount Number of category classes (e.g., 3)
     * @return List of paired numerical values grouped by annotator index
     */
    public static List<List<List<Integer>>> mapLabelsToNumbers(List<List<List<String>>> annotations, Integer categoryCount) {
        if (annotations == null || categoryCount == null || categoryCount <= 0) {
            return new ArrayList<>();
        }

        // Find the maximum number of annotators across all couples
        int maxAnnotators = 0;
        for (List<List<String>> coupleAnnotations : annotations) {
            maxAnnotators = Math.max(maxAnnotators, coupleAnnotations.size());
        }

        // Collect unique labels
        Set<String> uniqueLabels = new LinkedHashSet<>();
        for (List<List<String>> coupleAnnotations : annotations) {
            for (List<String> pair : coupleAnnotations) {
                for (String label : pair) {
                    if (label != null && !label.trim().isEmpty()) {
                        uniqueLabels.add(label);
                    }
                }
            }
        }

        // Build label-to-number mapping
        Map<String, Integer> labelMap = new HashMap<>();
        int nextNumber = 0;
        for (String label : uniqueLabels) {
            if (nextNumber < categoryCount) {
                labelMap.put(label, nextNumber++);
            } else {
                labelMap.put(label, nextNumber++);
            }
        }
        labelMap.put("not annotated", nextNumber);
        labelMap.put("", nextNumber);

        // Regroup annotations by annotator index and map to numbers
        List<List<List<Integer>>> result = new ArrayList<>();
        for (int annotatorIndex = 0; annotatorIndex < maxAnnotators; annotatorIndex++) {
            List<List<Integer>> annotatorPairs = new ArrayList<>();
            for (List<List<String>> coupleAnnotations : annotations) {
                if (annotatorIndex < coupleAnnotations.size() && coupleAnnotations.get(annotatorIndex).size() == 2) {
                    List<String> pair = coupleAnnotations.get(annotatorIndex);
                    String annotatorLabel = pair.get(0) != null && !pair.get(0).trim().isEmpty() ? pair.get(0) : "not annotated";
                    String adminLabel = pair.get(1) != null && !pair.get(1).trim().isEmpty() ? pair.get(1) : "not annotated";
                    Integer annotatorNumber = labelMap.get(annotatorLabel);
                    Integer adminNumber = labelMap.get(adminLabel);
                    annotatorPairs.add(List.of(annotatorNumber, adminNumber));
                } else {
                    annotatorPairs.add(List.of(labelMap.get("not annotated"), labelMap.get("not annotated")));
                }
            }
            if (!annotatorPairs.isEmpty()) {
                result.add(annotatorPairs);
            }
        }
        return result;
    }
}