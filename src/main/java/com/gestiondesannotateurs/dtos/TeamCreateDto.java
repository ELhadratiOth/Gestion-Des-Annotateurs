package com.gestiondesannotateurs.dtos;


import com.gestiondesannotateurs.entities.Annotator;

import java.time.LocalDateTime;
import java.util.List;

public record TeamCreateDto(
		String datasetName,
		Double datasetAdvancement,
		LocalDateTime datasetCreatedAt,
		String labelName,
		List<AnnotatorTeamDto> annotators
) {
}