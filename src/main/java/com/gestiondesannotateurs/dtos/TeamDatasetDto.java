package com.gestiondesannotateurs.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.SQLType;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TeamDatasetDto {
    private String datasetName;
    private long annotatorCount;
}