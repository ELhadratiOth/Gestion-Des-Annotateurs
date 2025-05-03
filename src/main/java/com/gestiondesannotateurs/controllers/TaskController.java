package com.gestiondesannotateurs.controllers;

import com.gestiondesannotateurs.dtos.TaskCreate;
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
        return GlobalSuccessHandler.success("Liste des tâches récupérée avec succès", tasks);
    }

    @PostMapping
    public ResponseEntity<GlobalResponse<String>> createTask(@RequestBody @Valid TaskCreate taskCreate) {
        taskService.createTask(taskCreate);
        return GlobalSuccessHandler.created("Tâche créée avec succès");
    }
}