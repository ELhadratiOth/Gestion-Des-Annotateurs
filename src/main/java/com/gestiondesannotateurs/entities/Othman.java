package com.gestiondesannotateurs.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("SUPER_ADMIN")
@Getter
@Setter
//@AllArgsConstructor
//@NoArgsConstructor
public class Othman extends Person {
}

