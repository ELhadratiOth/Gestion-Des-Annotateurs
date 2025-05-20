package com.gestiondesannotateurs.services;

import com.gestiondesannotateurs.interfaces.KappaEvaluationService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class KappaEvaluationServiceImpl implements KappaEvaluationService {

    @Override
    public double calculateKappa(List<List<Integer>> annotations, int numberOfCategories) {
        validateInput(annotations, numberOfCategories);
        int annotatorCount = annotations.get(0).size();

        return annotatorCount == 2
                ? calculateCohensKappa(annotations, numberOfCategories)
                : calculateFleissKappa(annotations, numberOfCategories);
    }

    private void validateInput(List<List<Integer>> annotations, int numberOfCategories) {
        if (annotations == null || annotations.isEmpty() || annotations.get(0).size() < 2) {
            throw new IllegalArgumentException("At least 2 annotators and 1 item required");
        }
        if (numberOfCategories < 2) {
            throw new IllegalArgumentException("At least 2 categories required");
        }

        annotations.forEach(item -> {
            if (item.size() != annotations.get(0).size()) {
                throw new IllegalArgumentException("All items must have same number of annotations");
            }
            item.forEach(label -> {
                if (label >= numberOfCategories || label < 0) {
                    throw new IllegalArgumentException(
                            String.format("Label %d is invalid for numberOfCategories=%d",
                                    label, numberOfCategories));
                }
            });
        });
    }

    private double calculateCohensKappa(List<List<Integer>> annotations, int numberOfCategories) {
        List<Integer> annotator1 = annotations.stream()
                .map(item -> item.get(0))
                .collect(Collectors.toList());
        List<Integer> annotator2 = annotations.stream()
                .map(item -> item.get(1))
                .collect(Collectors.toList());

        int n = annotator1.size();
        int[][] matrix = new int[numberOfCategories][numberOfCategories];

        for (int i = 0; i < n; i++) {
            matrix[annotator1.get(i)][annotator2.get(i)]++;
        }

        double po = 0.0;
        for (int i = 0; i < numberOfCategories; i++) {
            po += matrix[i][i];
        }
        po /= n;

        double pe = 0.0;
        int[] rowSums = new int[numberOfCategories];
        int[] colSums = new int[numberOfCategories];

        for (int i = 0; i < numberOfCategories; i++) {
            for (int j = 0; j < numberOfCategories; j++) {
                rowSums[i] += matrix[i][j];
                colSums[j] += matrix[i][j];
            }
        }

        for (int i = 0; i < numberOfCategories; i++) {
            pe += (rowSums[i] * colSums[i]);
        }
        pe /= (n * n);

        return (po - pe) / (1 - pe);
    }

    private double calculateFleissKappa(List<List<Integer>> annotations, int numberOfCategories) {
        int n = annotations.size();
        int m = annotations.get(0).size();

        int[][] counts = new int[n][numberOfCategories];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                counts[i][annotations.get(i).get(j)]++;
            }
        }

        double[] Pj = new double[numberOfCategories];
        for (int j = 0; j < numberOfCategories; j++) {
            for (int i = 0; i < n; i++) {
                Pj[j] += counts[i][j];
            }
            Pj[j] /= (n * m);
        }

        double Pbar = 0.0;
        for (int i = 0; i < n; i++) {
            double sum = 0.0;
            for (int j = 0; j < numberOfCategories; j++) {
                sum += counts[i][j] * (counts[i][j] - 1);
            }
            Pbar += sum / (m * (m - 1));
        }
        Pbar /= n;

        double Pexp = 0.0;
        for (double pj : Pj) {
            Pexp += pj * pj;
        }

        if (Math.abs(1.0 - Pexp) < 1e-10) {
            return 1.0;
        }

        return (Pbar - Pexp) / (1 - Pexp);
    }

    private void validateSingleItemAnnotations(List<Integer> annotations,
                                               Map<Integer, String> categoryLabels) {
        if (annotations == null || categoryLabels == null) {
            throw new IllegalArgumentException("Annotations and labels cannot be null");
        }

        if (annotations.size() < 2) {
            throw new IllegalArgumentException("At least 2 annotations are required");
        }

        if (categoryLabels.size() < 2) {
            throw new IllegalArgumentException("At least 2 categories must be defined");
        }

        for (Integer annotation : annotations) {
            if (annotation == null || !categoryLabels.containsKey(annotation)) {
                throw new IllegalArgumentException(
                        "Invalid annotation: " + annotation +
                                ". Valid categories: " + categoryLabels.keySet()
                );
            }
        }
    }

    private double calculateFleissKappaForSingleItem(List<Integer> annotations, int numberOfCategories) {
        int numAnnotations = annotations.size();
        int[][] counts = new int[1][numberOfCategories];

        for (int annotation : annotations) {
            counts[0][annotation]++;
        }

        double[] Pj = new double[numberOfCategories];
        for (int j = 0; j < numberOfCategories; j++) {
            Pj[j] = counts[0][j] / (double) numAnnotations;
        }

        double Pbar = 0.0;
        for (int j = 0; j < numberOfCategories; j++) {
            Pbar += counts[0][j] * (counts[0][j] - 1);
        }
        Pbar /= numAnnotations * (numAnnotations - 1);

        double Pexp = 0.0;
        for (double pj : Pj) {
            Pexp += pj * pj;
        }

        if (Math.abs(1.0 - Pexp) < 1e-10) {
            return 1.0;
        }

        return (Pbar - Pexp) / (1 - Pexp);
    }

    @Override
    public String getMostFrequentCategoryWithKappa(List<Integer> singleItemAnnotations,
                                                   Map<Integer, String> categoryLabels) {
        validateSingleItemAnnotations(singleItemAnnotations, categoryLabels);

        double kappa = calculateFleissKappaForSingleItem(singleItemAnnotations, categoryLabels.size());

        Map<Integer, Long> frequencyMap = singleItemAnnotations.stream()
                .collect(Collectors.groupingBy(
                        annotation -> annotation,
                        Collectors.counting()
                ));

        int mostFrequentCategory = frequencyMap.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElseThrow(() -> new IllegalStateException("Could not determine category"));

        // Formatage avec point comme séparateur décimal
        return String.format("%s (Kappa: %.2f)",
                categoryLabels.get(mostFrequentCategory),
                kappa).replace(",", ".");
    }
}