package com.gestiondesannotateurs.repositories;

import com.gestiondesannotateurs.entities.Dataset;
import com.gestiondesannotateurs.entities.Label;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface LabelRepo extends JpaRepository<Label,Long> {
    List<Label> findAllByDeletedFalse();
}
