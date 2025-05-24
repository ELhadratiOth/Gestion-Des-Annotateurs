package com.gestiondesannotateurs.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;
@Getter
@Setter
public class AnnotationDto {
    @NotNull
    private Long coupletextId;
    @NotNull
    private Long annotatorId;
    private String label; // "similar" ou "not_similar"
    private String textA ;
    private String textB ;
}
