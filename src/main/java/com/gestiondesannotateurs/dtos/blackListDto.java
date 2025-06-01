package com.gestiondesannotateurs.dtos;

import lombok.*;

import java.time.LocalDateTime;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class blackListDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private boolean active;
    private boolean isSpammer;

    private LocalDateTime detectionDate;

}