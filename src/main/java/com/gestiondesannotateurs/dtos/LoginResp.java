package com.gestiondesannotateurs.dtos;

public record LoginResp (
        String token ,
        PersonDTO user
) {
}
