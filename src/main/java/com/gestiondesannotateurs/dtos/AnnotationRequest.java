package com.gestiondesannotateurs.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnnotationRequest {
    private Long coupletextId;
    @NotBlank(message = "You must choose a label before submitting.")
    private String label;

}
