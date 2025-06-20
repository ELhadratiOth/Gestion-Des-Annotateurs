package com.gestiondesannotateurs.interfaces;

import com.gestiondesannotateurs.dtos.*;
import com.gestiondesannotateurs.entities.Coupletext;
import com.gestiondesannotateurs.entities.Dataset;
import com.gestiondesannotateurs.entities.TaskToDo;
import org.springframework.scheduling.config.Task;

import java.util.List;

public interface TaskService {
    public void createTask(TaskCreate task);
    public List<TaskToDo> getAll();
    public List<TaskToDo> getTasksByAnnotatorId(Long annotatorId);
    public List<TaskToDoDto> getTasksByDatasetId(Long datasetId);
    Coupletext getNextUnannotatedCoupletextForTask(Long taskId);
    void deleteTaskByDatasetId(Long datasetId);
    public double getProgressForTask(Long taskId, Long annotatorId);
    public List<Coupletext> getDuplicatedCoupletextsForTask(Long taskId);
    LastFinishedTask lastCompletedTask();
//    public void deleteTask(Long taskId);
//    void deleteTasksByDataset(Dataset dataset);
}
