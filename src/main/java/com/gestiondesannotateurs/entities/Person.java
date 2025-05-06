package com.gestiondesannotateurs.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "user_type")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(unique = true, nullable = false)
    private String email;


    @Column(unique = true, nullable = false)
    private String login;


    @Column(nullable = false)
    private String password = "111";

    @Column(nullable = false)
    private boolean active = true;

    @PrePersist
    @PreUpdate
    private void normalizeFields() {
        if (email != null) {
            email = email.toLowerCase().trim();
        }
        if (login != null) {
            login = login.toLowerCase().trim();
        }
    }
}