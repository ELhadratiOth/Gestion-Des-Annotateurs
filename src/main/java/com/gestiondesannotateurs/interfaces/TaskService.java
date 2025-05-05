package com.gestiondesannotateurs.interfaces;

import com.gestiondesannotateurs.dtos.DatasetInfo;
import com.gestiondesannotateurs.dtos.TaskCreate;
import com.gestiondesannotateurs.entities.Dataset;
import com.gestiondesannotateurs.entities.TaskToDo;
import org.springframework.scheduling.config.Task;

import java.util.List;

public interface TaskService {
    public void createTask(TaskCreate task);
    public List<TaskToDo> getAll();
    public List<TaskToDo> getTasksByAnnotatorId(Long annotatorId);
    public List<TaskToDo> getTasksByDatasetId(Long datasetId);
    public void deleteTask(Long taskId);
    

}
