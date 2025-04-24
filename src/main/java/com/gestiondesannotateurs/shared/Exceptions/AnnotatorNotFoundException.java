package com.gestiondesannotateurs.shared.Exceptions;

public class AnnotatorNotFoundException extends RuntimeException {
    public AnnotatorNotFoundException(String message) {
        super(message);
    }
}