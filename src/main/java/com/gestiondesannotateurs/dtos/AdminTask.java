package com.gestiondesannotateurs.dtos;

public record AdminTask(
        Long datasetId,
        String DatasetName,
        String DatasetDescription,
        String LabelName ,
        Double Advancement,
        String Status ,
        String Action,
        Long baseAffectedRows
//        String AvailableLabelClasses

) {
}
