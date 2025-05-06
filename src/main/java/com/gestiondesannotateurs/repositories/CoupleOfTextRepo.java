package com.gestiondesannotateurs.repositories;

import com.gestiondesannotateurs.entities.Coupletext;
import com.gestiondesannotateurs.entities.Dataset;
import com.gestiondesannotateurs.entities.TaskToDo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface CoupleOfTextRepo extends JpaRepository<Coupletext,Long> {
    List<Coupletext> findByDataset(Dataset dataset);

    List<Coupletext> findByTasksContaining(TaskToDo task);
}
