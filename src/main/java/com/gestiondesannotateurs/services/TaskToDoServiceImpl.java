package com.gestiondesannotateurs.services;

import java.util.List;
import java.util.Optional;

import com.gestiondesannotateurs.dtos.TaskCreate;
import com.gestiondesannotateurs.entities.Annotator;
import com.gestiondesannotateurs.entities.Dataset;
import com.gestiondesannotateurs.entities.Label;
import com.gestiondesannotateurs.interfaces.TaskService;
import com.gestiondesannotateurs.repositories.AnnotatorRepo;
import com.gestiondesannotateurs.repositories.DatasetRepo;
import com.gestiondesannotateurs.repositories.LabelRepo;
import com.gestiondesannotateurs.shared.Exceptions.CustomResponseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.config.Task;
import org.springframework.stereotype.Service;

import com.gestiondesannotateurs.entities.TaskToDo;
import com.gestiondesannotateurs.repositories.TaskToDoRepo;

@Service
public class TaskToDoServiceImpl implements TaskService {

    @Autowired
    private DatasetRepo datasetRepo ;

    @Autowired
    private AnnotatorRepo annotatorRepo;

    @Autowired
    private TaskToDoRepo taskToDoRepo ;

    @Autowired
    private LabelRepo labelRepo;


    @Override
    public void createTask(TaskCreate tasks) {
        Optional<Dataset> dataset =  datasetRepo.findById(tasks.datasetId());
        if (dataset.isEmpty()){
            throw new RuntimeException("No such dataset");
        }
        for (Long annotator_id : tasks.annotatorId()){
            Optional<Annotator> annotator = annotatorRepo.findById(annotator_id) ;
            if (annotator.isEmpty()){
                throw new RuntimeException("No such annotator");
            }
            TaskToDo task =  new TaskToDo();
            task.setAnnotator(annotator.get());
            task.setDataset(dataset.get());
            taskToDoRepo.save(task);
        }
    }

    @Override
    public List<TaskToDo> getAll() {
        List<TaskToDo> tasks = taskToDoRepo.findAll();
        return tasks;
    }




    public List<TaskToDo> getTasksByAnnotatorId(Long annotatorId) {

        return taskToDoRepo.findByAnnotatorId(annotatorId);
    }

    public List<TaskToDo> getTasksByDatasetId(Long datasetId) {
        return taskToDoRepo.findByDatasetId(datasetId);
    }
    @Override
    public void deleteTask(Long taskId) {
        TaskToDo task = taskToDoRepo.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        taskToDoRepo.delete(task);
    }


}
