package com.gestiondesannotateurs.utils;

import java.util.ArrayList;
import java.util.List;

public class AnnotationTransformer {

    /**
     * Transforms a list of annotation lists into a list of paired annotations per couple of text.
     * Input format: [[admin_label, annotator1_label, annotator2_label, ...], ...]
     * Output format: [[[annotator1_label, admin_label], [annotator2_label, admin_label], ...], ...]
     * Each inner list corresponds to a couple of text, with pairs of [annotator_label, admin_label].
     * Skips empty lists, invalid admin labels ("N/A"), and "N/A" annotator labels.
     *
     * @param inputAnnotations List of lists containing admin and annotator labels
     * @return Transformed list of paired annotations
     */
    public static List<List<List<String>>> transformAnnotations(List<List<String>> inputAnnotations) {
        List<List<List<String>>> result = new ArrayList<>();

        for (List<String> annotationList : inputAnnotations) {
            // Skip null or empty lists
            if (annotationList == null || annotationList.isEmpty()) {
                System.out.println("Warning: Skipping empty or null annotation list");
                continue;
            }

            // Ensure at least one label (admin's)
            if (annotationList.size() < 1) {
                System.out.println("Warning: Skipping invalid annotation list with no admin label: " + annotationList);
                continue;
            }

            // Get admin label (first element)
            String adminLabel = annotationList.get(0) != null ? annotationList.get(0) : "N/A";
            if (adminLabel.equals("N/A")) {
                System.out.println("Warning: Skipping annotation list with invalid admin label: " + annotationList);
                continue;
            }

            // Create pairs for each annotator's label
            List<List<String>> pairedAnnotations = new ArrayList<>();
            for (int i = 1; i < annotationList.size(); i++) {
                String annotatorLabel = annotationList.get(i) != null ? annotationList.get(i) : "N/A";
                // Only include valid annotator labels
                if (!annotatorLabel.equals("N/A")) {
                    List<String> pair = List.of(annotatorLabel, adminLabel);
                    pairedAnnotations.add(pair);
                }
            }

            // Add paired annotations for this couple of text if any valid pairs exist
            if (!pairedAnnotations.isEmpty()) {
                result.add(pairedAnnotations);
            }
        }

        return result;
    }
}

//[[[NOT SIM, SIM], [SIM, SIM], [SIM, SIM], [NOT SIM, SIM]], [[NOT SIM, NOT SIM], [SIM, NOT SIM], [NOT SIM, NOT SIM], [SIM, NOT SIM]]]