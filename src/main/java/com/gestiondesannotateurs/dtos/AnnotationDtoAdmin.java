package com.gestiondesannotateurs.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnnotationDtoAdmin {
    @NotNull
    private Long coupletextId;
    @NotNull
    private Long annotatorId;
    private String label; // "similar" ou "not_similar"
    private String textA ;
    private String textB ;
    private Boolean isAdmin = true;
}
