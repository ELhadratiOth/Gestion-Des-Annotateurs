package com.gestiondesannotateurs.dtos;

public record CoupleOfTextWithAnnotation(
        Long coupleOfTextId,
        Long annotationId,
        String textA,
        String textB,
        String annotationLabel,
        String datasetName,
        String datasetLabelName,
        String datasetLabelClasses
) {
}
