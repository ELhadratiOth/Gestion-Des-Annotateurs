package com.gestiondesannotateurs.repositories;

import com.gestiondesannotateurs.dtos.TeamDatasetDto;
import com.gestiondesannotateurs.entities.Dataset;
import com.gestiondesannotateurs.entities.TaskToDo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface DatasetRepo extends JpaRepository<Dataset,Long> {
    @Query("SELECT NEW com.gestiondesannotateurs.dtos.TeamDatasetDto(d.name, COUNT(DISTINCT t.annotator)) " +
            "FROM Dataset d " +
            "JOIN d.tasks t " +
            "WHERE d.id IN (SELECT DISTINCT t2.dataset.id FROM TaskToDo t2 WHERE t2.annotator.id = :annotatorId) " +
            "GROUP BY d.name")
    List<TeamDatasetDto> findSharedDatasetsWithAnnotatorCount(Long annotatorId);

    @Query("SELECT d FROM Dataset d WHERE d.advancement = 100.0")
    List<Dataset> findAllWithAdvancement100();

    @Query("SELECT d FROM Dataset d WHERE d.advancement != 100.0")
    List<Dataset> findAllWithAdvancementNot100();

    @Query("SELECT t FROM Dataset t " +
            "ORDER BY t.id DESC LIMIT 1")
    Optional<Dataset> findLastAddedDataset();

}
