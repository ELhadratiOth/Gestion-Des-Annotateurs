package com.gestiondesannotateurs.shared.Exceptions;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class AdminNotFoundException extends RuntimeException {
    public AdminNotFoundException(Long id) {
        super("Admin" + id + " introuvable");
    }
}