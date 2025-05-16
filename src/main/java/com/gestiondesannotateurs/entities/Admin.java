package com.gestiondesannotateurs.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;

@Entity
@DiscriminatorValue("ADMIN")
@Getter
@Setter
public class Admin extends Person {



}
