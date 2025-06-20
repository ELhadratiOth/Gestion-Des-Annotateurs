package com.gestiondesannotateurs.dtos;

import com.gestiondesannotateurs.entities.Annotator;

import java.time.LocalDateTime;
import java.util.List;

public record DatasetInfo(
		Long datasetId,
		Long datasetSize,
		Double datsetSizeMB,
		String datasetName,
		Double datasetAdvancement,
		String datasetDescription,
		String datasetLabel,
		LocalDateTime datasetCreatedAt,
		Boolean assigned,
		List<Annotator> annotators,
		LocalDateTime assignedAt
) {
}