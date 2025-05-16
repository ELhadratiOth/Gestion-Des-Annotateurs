package com.gestiondesannotateurs.repositories;

import com.gestiondesannotateurs.entities.Admin;
import com.gestiondesannotateurs.entities.Othman;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface OthmanRepo extends JpaRepository<Othman,Long> {
    boolean existsByEmail(String email);
//    boolean existsByUsername(String newLogin);
}
