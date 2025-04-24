package com.gestiondesannotateurs.repositories;

import com.gestiondesannotateurs.entities.Dataset;
import com.gestiondesannotateurs.entities.Label;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface LabelRepo extends JpaRepository<Label,Long> {
}
