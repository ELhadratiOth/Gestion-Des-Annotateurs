package com.gestiondesannotateurs.dtos;

import com.gestiondesannotateurs.entities.Annotator;
import lombok.Getter; // Si vous utilisez Lombok

@Getter // Lombok génère automatiquement les getters
public class AnnotatorWithTaskId {
    private final Long taskId;
    private final Annotator annotator;

    public AnnotatorWithTaskId(Long taskId, Annotator annotator) {
        this.taskId = taskId;
        this.annotator = annotator;
    }
}