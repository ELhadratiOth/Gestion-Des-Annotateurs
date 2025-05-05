package com.gestiondesannotateurs.repositories;

import com.gestiondesannotateurs.entities.TaskToDo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface TaskToDoRepo extends JpaRepository<TaskToDo,Long> {
    List<TaskToDo> findByAnnotator(Long annotatorId);

    List<TaskToDo> findByDataset(Long datasetId);
}
