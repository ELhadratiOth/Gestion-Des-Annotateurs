package com.gestiondesannotateurs.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnnotationResponse {
    private Long coupletextId;
    private Long annotatorId;
    private String textA;
    private String textB;
    private String label;

}
