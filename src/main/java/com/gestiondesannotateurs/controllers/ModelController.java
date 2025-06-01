package com.gestiondesannotateurs.controllers;

import com.gestiondesannotateurs.dtos.TrainRequest;
import com.gestiondesannotateurs.interfaces.ModelService;
import com.gestiondesannotateurs.shared.Exceptions.GlobalSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/model")
public class ModelController {

    @Autowired
    private ModelService modelService;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    private int datasetId;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFullDataset(
            @RequestParam("file") MultipartFile file,
            @RequestParam("task") String task,
            @RequestParam("project_name") String projectName,
            @RequestParam("learning_rate") double learningRate,
            @RequestParam("epochs") int epochs,
            @RequestParam("batch_size") int batchSize,
            @RequestParam("user") String user
    ) {
        try {
            TrainRequest request = new TrainRequest(file, task, projectName,learningRate, epochs, batchSize, user);
            String response = modelService.uploadFullDataset(request);
            return GlobalSuccessHandler.success("Training params and dataset upload succesfully",response);
        } catch (IOException e) {
            return GlobalSuccessHandler.success("Internal server error during dataset upload");
        }
    }

    @PostMapping("/train/{projectName}/{datasetId}")
    public ResponseEntity<?> launchTraining(@PathVariable int datasetId,@PathVariable String projectName) {
        String response = modelService.launchTraining(datasetId,projectName);
        return GlobalSuccessHandler.success(response);
    }

    @PostMapping("/test/{projectName}/{datasetId}")
    public ResponseEntity<?> launchTesting(@PathVariable int datasetId,@PathVariable String projectName) {
        String response = modelService.launchTesting(datasetId,projectName);
        return GlobalSuccessHandler.success(response);
    }

    @GetMapping("/train/history/{projectName}/{datasetId}")
    public ResponseEntity<?> getTrainingHistory(@PathVariable int datasetId,@PathVariable String projectName) {
        String response = modelService.getTrainingHistory(datasetId,projectName);
        return GlobalSuccessHandler.success(response);
    }

    @GetMapping("/test/history/{projectName}/{datasetId}")
    public ResponseEntity<?> getTestingHistory(@PathVariable int datasetId,@PathVariable String projectName) {
        this.datasetId = datasetId;
        String response = modelService.getTestingHistory(datasetId,projectName);
        return GlobalSuccessHandler.success(response);
    }

    @GetMapping("/projects/history")
    public ResponseEntity<?> getHistory() {
        this.datasetId = datasetId;
        String response = modelService.getHistory();
        return GlobalSuccessHandler.success(response);
    }


    @PostMapping("/logs/receive")
    public void receiveLog(@RequestBody String log) {
        messagingTemplate.convertAndSend("/topic/logs", log);
    }

}
