package com.gestiondesannotateurs.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gestiondesannotateurs.entities.TaskToDo;
import com.gestiondesannotateurs.repositories.TaskToDoRepo;

@Service
public class TaskToDoServiceImpl  {
	@Autowired
    private  TaskToDoRepo taskToDoRepository;

    public List<TaskToDo> getAllTasks() {
        return taskToDoRepository.findAll();
    }

    public  Optional<TaskToDo> getTaskById(Long id) {
        return taskToDoRepository.findById(id);


    }

    public TaskToDo createTask(TaskToDo taskToDo) {
        return taskToDoRepository.save(taskToDo);
    }

    public void deleteTask(Long id) {
        taskToDoRepository.deleteById(id);
    }

    public List<TaskToDo> getTasksByAnnotator(Long annotatorId) {
    	
        return taskToDoRepository.findByAnnotator(annotatorId);
    }

    public List<TaskToDo> getTasksByDataset(Long datasetId) {
        return taskToDoRepository.findByDataset(datasetId);
    }
    public List<TaskToDo> getTasksByAnnotatorAndDataset(Long annotatorId, Long datasetId) {
		return taskToDoRepository.findByAnnotatorAndDataset(annotatorId, datasetId);
	}
	

}
