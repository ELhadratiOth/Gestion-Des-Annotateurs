package com.gestiondesannotateurs.services;

import java.util.List;
import java.util.Optional;

import com.gestiondesannotateurs.dtos.TaskCreate;
import com.gestiondesannotateurs.entities.Annotator;
import com.gestiondesannotateurs.entities.Dataset;
import com.gestiondesannotateurs.interfaces.TaskService;
import com.gestiondesannotateurs.repositories.AnnotatorRepo;
import com.gestiondesannotateurs.repositories.DatasetRepo;
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


    @Override
    public void createTask(TaskCreate tasks) {
        System.out.println("hahiya dataset");

        System.out.println(tasks.dataset_id());

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

    @Override
    public List<TaskToDo> getAll() {
        List<TaskToDo> tasks = taskToDoRepo.findAll();
        return tasks;
    }


//    public List<TaskToDo> getTasksByAnnotator(Long annotatorId) {
//
//        return taskToDoRepository.findByAnnotator(annotatorId);
//    }
//
//    public List<TaskToDo> getTasksByDataset(Long datasetId) {
//        return taskToDoRepository.findByDataset(datasetId);
//    }
//    public List<TaskToDo> getTasksByAnnotatorAndDataset(Long annotatorId, Long datasetId) {
//		return taskToDoRepository.findByAnnotatorAndDataset(annotatorId, datasetId);
//	}
	

}
