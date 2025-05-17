package com.gestiondesannotateurs.services;
import com.gestiondesannotateurs.interfaces.KappaEvaluationService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class KappaEvaluationServiceImpl implements KappaEvaluationService {

    public double calculateKappa(List<List<Integer>> annotations) {
        if (annotations == null || annotations.size() < 2) {
            throw new IllegalArgumentException("At least 2 annotators required");
        }
        int annotatorCount = annotations.get(0).size();

        return annotatorCount == 2
                ? calculateCohensKappa(annotations)
                : calculateFleissKappa(annotations);
    }

    private double calculateCohensKappa(List<List<Integer>> annotations) {
        List<Integer> annotator1 = annotations.stream()
                .map(item -> item.get(0))
                .collect(Collectors.toList());

        List<Integer> annotator2 = annotations.stream()
                .map(item -> item.get(1))
                .collect(Collectors.toList());

        int n = annotator1.size();
        int[][] matrix = new int[3][3]; // 3 catégories (0,1,2)

        // Remplir la matrice de confusion
        for (int i = 0; i < n; i++) {
            matrix[annotator1.get(i)][annotator2.get(i)]++;
        }

        // Calculer l'accord observé (po)
        double po = 0.0;
        for (int i = 0; i < 3; i++) {
            po += matrix[i][i];
        }
        po /= n;

        // Calculer l'accord attendu (pe)
        double pe = 0.0;
        int[] rowSums = new int[3];
        int[] colSums = new int[3];

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                rowSums[i] += matrix[i][j];
                colSums[j] += matrix[i][j];
            }
        }

        for (int i = 0; i < 3; i++) {
            pe += (rowSums[i] * colSums[i]);
        }
        pe /= (n * n);

        return (po - pe) / (1 - pe);
    }

    private double calculateFleissKappa(List<List<Integer>> annotations) {
        int n = annotations.size();       // Nombre d'items
        int k = 3;                        // Nombre de catégories
        int m = annotations.get(0).size(); // Nombre d'annotateurs

        int[][] counts = new int[n][k];

        // Compter les votes par catégorie
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                int category = annotations.get(i).get(j);
                counts[i][category]++;
            }
        }

        // Calcul des proportions globales (Pj)
        double[] Pj = new double[k];
        for (int j = 0; j < k; j++) {
            for (int i = 0; i < n; i++) {
                Pj[j] += counts[i][j];
            }
            Pj[j] /= (n * m);
        }

        // Calcul de l'accord moyen (Pbar)
        double Pbar = 0.0;
        for (int i = 0; i < n; i++) {
            double sum = 0.0;
            for (int j = 0; j < k; j++) {
                sum += counts[i][j] * (counts[i][j] - 1);
            }
            Pbar += sum / (m * (m - 1));
        }
        Pbar /= n;

        // Calcul de l'accord attendu (Pexp)
        double Pexp = 0.0;
        for (double pj : Pj) {
            Pexp += pj * pj;
        }

        return (Pbar - Pexp) / (1 - Pexp);
    }
}