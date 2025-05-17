package com.gestiondesannotateurs.services;
import com.gestiondesannotateurs.interfaces.KappaEvaluationService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;

@Service
public class KappaEvaluationServiceImpl implements KappaEvaluationService {

    public double calculateKappa(List<List<Integer>> annotations) {
        if (annotations == null || annotations.isEmpty() || annotations.get(0).size() < 2) {
            throw new IllegalArgumentException("At least 2 annotators and 1 item required");
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

        // Détection dynamique des catégories
        Set<Integer> categories = new HashSet<>();
        categories.addAll(annotator1);
        categories.addAll(annotator2);
        int categoryCount = categories.size();

        int n = annotator1.size();
        int[][] matrix = new int[categoryCount][categoryCount];

        // Créer une correspondance entre les labels et les indices de matrice
        List<Integer> sortedCategories = new ArrayList<>(categories);
        Collections.sort(sortedCategories);

        // Remplir la matrice de confusion
        for (int i = 0; i < n; i++) {
            int row = sortedCategories.indexOf(annotator1.get(i));
            int col = sortedCategories.indexOf(annotator2.get(i));
            matrix[row][col]++;
        }

        // Calculer l'accord observé (po)
        double po = 0.0;
        for (int i = 0; i < categoryCount; i++) {
            po += matrix[i][i];
        }
        po /= n;

        // Calculer l'accord attendu (pe)
        double pe = 0.0;
        int[] rowSums = new int[categoryCount];
        int[] colSums = new int[categoryCount];

        for (int i = 0; i < categoryCount; i++) {
            for (int j = 0; j < categoryCount; j++) {
                rowSums[i] += matrix[i][j];
                colSums[j] += matrix[i][j];
            }
        }

        for (int i = 0; i < categoryCount; i++) {
            pe += (rowSums[i] * colSums[i]);
        }
        pe /= (n * n);

        return (po - pe) / (1 - pe);
    }

    private double calculateFleissKappa(List<List<Integer>> annotations) {
        int n = annotations.size();       // Nombre d'items
        int m = annotations.get(0).size(); // Nombre d'annotateurs

        // Détection dynamique des catégories
        Set<Integer> uniqueLabels = new HashSet<>();
        for (List<Integer> item : annotations) {
            uniqueLabels.addAll(item);
        }
        int k = uniqueLabels.size();
        List<Integer> labels = new ArrayList<>(uniqueLabels);
        Collections.sort(labels);

        int[][] counts = new int[n][k];

        // Compter les votes par catégorie
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                int label = annotations.get(i).get(j);
                int labelIndex = labels.indexOf(label);
                counts[i][labelIndex]++;
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

        // Gestion des cas limites
        if (Pexp == 1.0) {
            return 1.0; // Accord parfait
        }

        return (Pbar - Pexp) / (1 - Pexp);
    }
}