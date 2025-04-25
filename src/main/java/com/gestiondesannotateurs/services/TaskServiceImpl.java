package com.gestiondesannotateurs.services;

import com.gestiondesannotateurs.dtos.TaskCreate;
import com.gestiondesannotateurs.entities.Annotator;
import com.gestiondesannotateurs.entities.Dataset;
import com.gestiondesannotateurs.entities.TaskToDo;
import com.gestiondesannotateurs.intefaces.TaskService;
import com.gestiondesannotateurs.repositories.AnnotatorRepo;
import com.gestiondesannotateurs.repositories.DatasetRepo;
import com.gestiondesannotateurs.repositories.TaskToDoRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskToDoRepo taskToDoRepo;

    @Autowired
    private AnnotatorRepo annotatorRepo ;

    @Autowired
    private DatasetRepo datasetRepo;


    @Override
    public void createTask(TaskCreate tasks) {

        Optional<Dataset> dataset =  datasetRepo.findById(tasks.dataset_id());
        if (dataset.isEmpty()){
            throw new RuntimeException("No such dataset");
        }


        for (Long annotator_id : tasks.annotator_id()){
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
}
