package com.gestiondesannotateurs.shared.Exceptions;

public class AnnotatorNotFoundException extends RuntimeException {
    public AnnotatorNotFoundException(Long id) {
        super("Annotateur " + id + " introuvable");
    }
}