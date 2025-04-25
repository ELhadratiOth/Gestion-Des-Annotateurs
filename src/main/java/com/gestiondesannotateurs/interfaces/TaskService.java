package com.gestiondesannotateurs.intefaces;

import com.gestiondesannotateurs.dtos.TaskCreate;
import com.gestiondesannotateurs.entities.Dataset;
import org.springframework.scheduling.config.Task;

public interface TaskService {
    public void createTask(TaskCreate task);
}
