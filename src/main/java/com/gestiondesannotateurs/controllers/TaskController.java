package com.gestiondesannotateurs.controllers;

import com.gestiondesannotateurs.dtos.LastFinishedTask;
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
import com.gestiondesannotateurs.utils.SecurityUtils;
import jakarta.validation.Valid;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")

public class TaskController {
    @Autowired
    private TaskService taskService;

    @Autowired
    private SecurityUtils securityUtils;


    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @GetMapping
    public ResponseEntity<GlobalResponse<List<TaskToDo>>> allTasks() {
        List<TaskToDo> tasks = taskService.getAll();
        return GlobalSuccessHandler.success("Successfully retrived tasks", tasks);
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @PostMapping
    public ResponseEntity<GlobalResponse<String>> createTask(@RequestBody @Valid TaskCreate taskCreate) {
        System.out.println(taskCreate);

        taskService.createTask(taskCreate);
        return GlobalSuccessHandler.created("Successfully created task");
    }

//    @PreAuthorize("hasAnyRole('ANNOTATOR')" , )
    @PreAuthorize("@securityUtils.isOwner(#annotatorId)")
    @GetMapping("/annotator/{annotatorId}")
    public ResponseEntity<GlobalResponse<List<TaskToDo>>> getTasksByAnnotatorId(@PathVariable Long annotatorId) {
		List<TaskToDo> tasks = taskService.getTasksByAnnotatorId(annotatorId);
		return GlobalSuccessHandler.success("Successfully retrived tasks", tasks);
	}

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN','ANNOTATOR')")

	@GetMapping("/dataset/{datasetId}")
	public ResponseEntity<GlobalResponse<List<TaskToDoDto>>> getTasksByDatasetId(@PathVariable Long datasetId) {
        List<TaskToDoDto> tasks = taskService.getTasksByDatasetId(datasetId);
		return GlobalSuccessHandler.success("Successfully retrived tasks", tasks);
	}


    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN','ANNOTATOR')")
    @GetMapping("/{taskId}/next")
    public ResponseEntity<?> getNextCoupletextToAnnotate(@PathVariable Long taskId) {
        Coupletext next = taskService.getNextUnannotatedCoupletextForTask(taskId);
        if (next == null) {
            return GlobalSuccessHandler.noContent();
        }
        return GlobalSuccessHandler.success("Next couple get sucessfully", next);
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @DeleteMapping("/dataset/{datasetId}")
    public ResponseEntity<?> deleteTasksByDatasetId(@PathVariable Long datasetId) {
         taskService.deleteTaskByDatasetId(datasetId);
         return GlobalSuccessHandler.deleted("Successfully deleted tasks only");
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @GetMapping("/last-task-completed")
    public ResponseEntity<?> getLastTaskCompleted() {
        System.out.println("hhhhhhhhhh");
        LastFinishedTask task = taskService.lastCompletedTask();
        return GlobalSuccessHandler.success("Last task completed successfully", task);
    }




}