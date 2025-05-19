package com.gestiondesannotateurs.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrainResponse {
    private String message;
    private String trainFile;
    private String testFile;
}
