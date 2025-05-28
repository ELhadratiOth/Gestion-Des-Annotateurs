package com.gestiondesannotateurs.dtos;

import java.time.LocalDateTime;

public record LastFinishedDataset(
         String datasetName,
         LocalDateTime createdAt,
         Double sizeMB,
         String labelName
) {
}
