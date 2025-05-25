package com.gestiondesannotateurs.controllers;

import com.gestiondesannotateurs.dtos.TaskCreate;
import com.gestiondesannotateurs.dtos.TaskToDoDto;
import com.gestiondesannotateurs.entities.Annotator;
import com.gestiondesannotateurs.entities.Coupletext;
import com.gestiondesannotateurs.entities.TaskToDo;
import com.gestiondesannotateurs.interfaces.AnnotationService;
import com.gestiondesannotateurs.interfaces.AnnotatorService;
import com.gestiondesannotateurs.interfaces.TaskService;
import com.gestiondesannotateurs.shared.Exceptions.GlobalSuccessHandler;
import com.gestiondesannotateurs.shared.GlobalResponse;
import jakarta.validation.Valid;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")

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
        System.out.println(taskCreate);

        taskService.createTask(taskCreate);
        return GlobalSuccessHandler.created("Successfully created task");
    }
    
    @GetMapping("/annotator/{annotatorId}")
    public ResponseEntity<GlobalResponse<List<TaskToDo>>> getTasksByAnnotatorId(@PathVariable Long annotatorId) {
		List<TaskToDo> tasks = taskService.getTasksByAnnotatorId(annotatorId);
		return GlobalSuccessHandler.success("Successfully retrived tasks", tasks);
	}

	@GetMapping("/dataset/{datasetId}")
	public ResponseEntity<GlobalResponse<List<TaskToDoDto>>> getTasksByDatasetId(@PathVariable Long datasetId) {
        List<TaskToDoDto> tasks = taskService.getTasksByDatasetId(datasetId);
		return GlobalSuccessHandler.success("Successfully retrived tasks", tasks);
	}

    @GetMapping("/{taskId}/next")
    public ResponseEntity<?> getNextCoupletextToAnnotate(@PathVariable Long taskId) {
        Coupletext next = taskService.getNextUnannotatedCoupletextForTask(taskId);
        if (next == null) {
            return GlobalSuccessHandler.noContent();
        }
        return GlobalSuccessHandler.success("Next couple get sucessfully", next);
    }

    @DeleteMapping("/dataset/{datasetId}")
    public ResponseEntity<?> deleteTasksByDatasetId(@PathVariable Long datasetId) {
         taskService.deleteTaskByDatasetId(datasetId);
         return GlobalSuccessHandler.deleted("Successfully deleted tasks only");
    }

    @GetMapping("/{annotatorId}/{taskId}")
    public ResponseEntity<?> getTaskProgressForAnnotator(@PathVariable Long taskId,@PathVariable Long annotatorId){
        double prog=taskService.getProgressForTask(taskId,annotatorId);
        return GlobalSuccessHandler.success("the progress of the the task "+taskId,prog);
    }

}