package com.gestiondesannotateurs.dtos;

public record AnnotatorTask(
        Long taskId,
        String datasetName,
        String datasetDescription,
        String datasetLabelName,
        Double progress,      // en pourcentage
        String status,        // Completed / Not Start / In Progress
        String action,        // Review / Start / Continue
        Long totalAssigned
) {}
