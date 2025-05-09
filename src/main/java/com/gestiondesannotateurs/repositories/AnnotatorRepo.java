package com.gestiondesannotateurs.repositories;

import com.gestiondesannotateurs.entities.Annotator;
import com.gestiondesannotateurs.entities.Coupletext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AnnotatorRepo extends JpaRepository<Annotator,Long> {
    boolean existsByEmail(String email);
    Annotator findByEmail(String email);
}
