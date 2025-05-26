package com.gestiondesannotateurs.dtos;

public record LastFinishedTask(
         String datasetName,
         String annotatorName,
         String finishedAt,
         Long nbrOfPendingTasks
) {
}
