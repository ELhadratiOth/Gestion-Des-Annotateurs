package com.gestiondesannotateurs.repositories;

import com.gestiondesannotateurs.entities.Dataset;
import com.gestiondesannotateurs.entities.TaskToDo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface TaskToDoRepo extends JpaRepository<TaskToDo,Long> {
    List<TaskToDo> findByAnnotatorId(Long annotatorId);

    List<TaskToDo> findByDatasetId(Long datasetId);

   void  deleteAllByDatasetId(Long dataset_id);

    List<TaskToDo> findByDataset(Dataset dataset);
}
