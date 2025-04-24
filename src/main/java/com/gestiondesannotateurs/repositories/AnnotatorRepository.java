package com.gestiondesannotateurs.repositories;

import com.gestiondesannotateurs.entities.Annotator;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnnotatorRepository extends JpaRepository<Annotator, Long> {

}