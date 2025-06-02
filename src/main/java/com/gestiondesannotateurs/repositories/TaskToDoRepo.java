package com.gestiondesannotateurs.repositories;

import com.gestiondesannotateurs.entities.Dataset;
import com.gestiondesannotateurs.entities.TaskToDo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface TaskToDoRepo extends JpaRepository<TaskToDo,Long> {
    List<TaskToDo> findByAnnotatorId(Long annotatorId);

    List<TaskToDo> findByDatasetId(Long datasetId);

   void  deleteAllByDatasetId(Long dataset_id);

    List<TaskToDo> findByDataset(Dataset dataset);

    @Query("SELECT t FROM TaskToDo t " +
            "WHERE t.status = 100.0 AND t.finishedAt IS NOT NULL " +
            "ORDER BY t.finishedAt DESC LIMIT 1")
    Optional<TaskToDo> findLastFinishedTask();

    @Query("SELECT COUNT(t) FROM TaskToDo t WHERE t.status <> 100.0  ")
    Long  CountNonFinishedTasks();


    @Query("SELECT EXISTS ( SELECT 1 FROM TaskToDo t WHERE t.annotator.id = :annotatorId AND t.status <> 100.0)")
    boolean hasNonTerminatedTasks(@Param("annotatorId") Long annotatorId);

    List<TaskToDo> findByAnnotatorIdAndStatusLessThan(Long annotatorId, double status);}



}

