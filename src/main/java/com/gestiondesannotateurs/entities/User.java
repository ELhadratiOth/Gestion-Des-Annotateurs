package com.gestiondesannotateurs.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import javax.management.relation.Role;

@Getter
@Setter
@Entity
    @Table(name = "users")
    public class User {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false)
        private String nom;

        @Column(nullable = false)
        private String prenom;

        @Column(nullable = false, unique = true)
        private String email;


        @Column(nullable = false)
        private String password;

        @ManyToOne
        @JoinColumn(name = "role_id", nullable = false)
        private Role role;

        // Constructeurs, getters et setters
        public User() {}

        public User(String nom, String prenom, String email, String password, Role role) {
            this.nom = nom;
            this.prenom = prenom;
            this.email = email;
            this.password = password;
            this.role = role;
        }

        // Getters et setters...
    }
}
