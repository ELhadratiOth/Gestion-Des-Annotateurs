package com.gestiondesannotateurs.shared.Exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnnotatorNotFoundException extends RuntimeException {
    public AnnotatorNotFoundException(Long id) {
        super("Annotator " + id + " Not Found");
    }
}