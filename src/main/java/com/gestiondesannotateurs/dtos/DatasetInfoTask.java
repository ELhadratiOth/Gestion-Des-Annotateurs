package com.gestiondesannotateurs.dtos;

import com.gestiondesannotateurs.entities.Annotator;

import java.time.LocalDateTime;
import java.util.List;

public record DatasetInfoTask(
		Long datasetId,
		Long datasetSize,
		Double datsetSizeMB,
		String datasetName,
		Double datasetAdvancement,
		String datasetDescription,
		String datasetLabel,
		LocalDateTime datasetCreatedAt,
		Boolean assigned
) {
}