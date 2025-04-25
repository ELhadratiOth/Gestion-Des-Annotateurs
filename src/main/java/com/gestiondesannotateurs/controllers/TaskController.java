package com.gestiondesannotateurs.controllers;


import com.gestiondesannotateurs.dtos.LabelCreate;
import com.gestiondesannotateurs.dtos.LabelResponse;
import com.gestiondesannotateurs.dtos.TaskCreate;
import com.gestiondesannotateurs.entities.Label;
import com.gestiondesannotateurs.intefaces.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    @Autowired
    private TaskService taskService;

    @PostMapping
    public ResponseEntity<?> createLabel(@RequestBody @Valid TaskCreate taskCreate) {
        taskService.createTask(taskCreate);



        return new ResponseEntity<>( "sf 9diti  gharad" , HttpStatus.CREATED);
    }
}
