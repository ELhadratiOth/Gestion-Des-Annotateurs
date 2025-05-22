package com.gestiondesannotateurs.dtos;

import java.time.LocalDateTime;

public record DatasetInfo(
		Long datasetId,
		Long datasetSize,
		Double datsetSizeMB,
		String datasetName,
		Double datasetAdvancement,
		String datasetDescription,
		String datasetLabel,
		LocalDateTime datasetCreatedAt
) {
}