package com.gestiondesannotateurs.controllers;

import com.gestiondesannotateurs.dtos.TaskCreate;
import com.gestiondesannotateurs.dtos.TaskToDoDto;
import com.gestiondesannotateurs.entities.TaskToDo;
import com.gestiondesannotateurs.interfaces.TaskService;
import com.gestiondesannotateurs.shared.Exceptions.GlobalSuccessHandler;
import com.gestiondesannotateurs.shared.GlobalResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")

public class TaskController {
    @Autowired
    private TaskService taskService;

    @GetMapping
    public ResponseEntity<GlobalResponse<List<TaskToDo>>> allTasks() {
        List<TaskToDo> tasks = taskService.getAll();
        return GlobalSuccessHandler.success("Successfully retrived tasks", tasks);
    }

    @PostMapping
    public ResponseEntity<GlobalResponse<String>> createTask(@RequestBody @Valid TaskCreate taskCreate) {
        taskService.createTask(taskCreate);
        return GlobalSuccessHandler.created("Successfully created task");
    }
    
    @GetMapping("/{annotatorId}")
    public ResponseEntity<GlobalResponse<List<TaskToDo>>> getTasksByAnnotatorId(@PathVariable Long annotatorId) {
		List<TaskToDo> tasks = taskService.getTasksByAnnotatorId(annotatorId);
		return GlobalSuccessHandler.success("Successfully retrived tasks", tasks);
	}

	@GetMapping("/dataset/{datasetId}")
	public ResponseEntity<GlobalResponse<List<TaskToDoDto>>> getTasksByDatasetId(@PathVariable Long datasetId) {

        List<TaskToDoDto> tasks = taskService.getTasksByDatasetId(datasetId);
		return GlobalSuccessHandler.success("Successfully retrived tasks", tasks);
	}
}