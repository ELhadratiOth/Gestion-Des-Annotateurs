package com.gestiondesannotateurs.dtos;

public record StatsResp(
        Long nbrOfAnnotators,
        Long activeAnnotators,
        Long totalUsers,


        Long totalTasks,


        Long assignedDatasets,
        Long pendingDatasets,
        Long nbrOfDatasets,
        Long finishedDatasets



        ) {
}
