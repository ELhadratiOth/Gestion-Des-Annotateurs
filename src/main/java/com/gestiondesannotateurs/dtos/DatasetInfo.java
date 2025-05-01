package com.gestiondesannotateurs.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

public record DatasetInfo(	 Long datasetId,
							   Long datasetSize,
							   String datasetName,
							   Double datasetAdvancement,
							   String datasetDescription,
							   String datasetLabel,
							   LocalDateTime datasetCreatedAt ) {

	
}
