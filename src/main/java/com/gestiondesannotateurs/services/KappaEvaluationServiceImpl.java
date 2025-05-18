package com.gestiondesannotateurs.services;

import com.gestiondesannotateurs.interfaces.KappaEvaluationService;
import org.springframework.stereotype.Service;

import java.util.List;
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

        // Fill confusion matrix
        for (int i = 0; i < n; i++) {
            matrix[annotator1.get(i)][annotator2.get(i)]++;
        }

        // Calculate observed agreement (po)
        double po = 0.0;
        for (int i = 0; i < numberOfCategories; i++) {
            po += matrix[i][i];
        }
        po /= n;

        // Calculate expected agreement (pe)
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
        int n = annotations.size();       // Number of items
        int m = annotations.get(0).size(); // Number of annotators

        int[][] counts = new int[n][numberOfCategories];

        // Count votes per category
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                counts[i][annotations.get(i).get(j)]++;
            }
        }

        // Calculate global proportions (Pj)
        double[] Pj = new double[numberOfCategories];
        for (int j = 0; j < numberOfCategories; j++) {
            for (int i = 0; i < n; i++) {
                Pj[j] += counts[i][j];
            }
            Pj[j] /= (n * m);
        }

        // Calculate average agreement (Pbar)
        double Pbar = 0.0;
        for (int i = 0; i < n; i++) {
            double sum = 0.0;
            for (int j = 0; j < numberOfCategories; j++) {
                sum += counts[i][j] * (counts[i][j] - 1);
            }
            Pbar += sum / (m * (m - 1));
        }
        Pbar /= n;

        // Calculate expected agreement (Pexp)
        double Pexp = 0.0;
        for (double pj : Pj) {
            Pexp += pj * pj;
        }

        // Handle edge cases
        if (Math.abs(1.0 - Pexp) < 1e-10) {
            return 1.0; // Perfect agreemen
        }

        return (Pbar - Pexp) / (1 - Pexp);
    }
}