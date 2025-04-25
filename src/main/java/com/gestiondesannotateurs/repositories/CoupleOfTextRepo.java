package com.gestiondesannotateurs.repositories;

import com.gestiondesannotateurs.entities.Coupletext;
import com.gestiondesannotateurs.entities.Dataset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CoupleOfTextRepo extends JpaRepository<Coupletext,Long> {
}
