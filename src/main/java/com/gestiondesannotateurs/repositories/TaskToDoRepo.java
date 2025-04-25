package com.gestiondesannotateurs.repositories;

import com.gestiondesannotateurs.entities.TaskToDo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface TaskToDoRepo extends JpaRepository<TaskToDo,Long> {
}
