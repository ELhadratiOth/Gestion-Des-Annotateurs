package com.gestiondesannotateurs.repositories;

import com.gestiondesannotateurs.entities.Annotator;
import com.gestiondesannotateurs.entities.Coupletext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface AnnotatorRepo extends JpaRepository<Annotator,Long> {
    boolean existsByEmail(String email);
    Annotator findByEmail(String email);
    List<Annotator> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String firstName, String lastName);
    @Query("SELECT a FROM Annotator a JOIN a.tasks t  WHERE t.id = :taskId")
    Optional<Annotator> getAnnotatorByTask(@Param("taskId") Long taskId);

    Annotator findTopByOrderByIdDesc();
}
