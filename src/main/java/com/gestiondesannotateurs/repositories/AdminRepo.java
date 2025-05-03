package com.gestiondesannotateurs.repositories;

import com.gestiondesannotateurs.entities.Admin;
import com.gestiondesannotateurs.entities.Annotator;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface AdminRepo extends JpaRepository<Admin,Long> {
    boolean existsByEmail(String email);
    boolean existsByLogin(String newLogin);
}
