package com.gestiondesannotateurs.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CoupletextDto {
    private Long id;
    private String textA;
    private String textB;
    private String trueLabel;
}