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

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFullDataset(
            @RequestParam("file") MultipartFile file,
            @RequestParam("task") String task,
            @RequestParam("learning_rate") double learningRate,
            @RequestParam("epochs") int epochs,
            @RequestParam("batch_size") int batchSize,
            @RequestParam("user") String user,
            @RequestParam("dataset_id") int datasetId
    ) {
        try {
            TrainRequest request = new TrainRequest(file, task, learningRate, epochs, batchSize, user, datasetId);
            String response = modelService.uploadFullDataset(request);
            return GlobalSuccessHandler.success(response);
        } catch (IOException e) {
            return GlobalSuccessHandler.success("Internal server error during dataset upload");
        }
    }

    @PostMapping("/train/{datasetId}")
    public ResponseEntity<?> launchTraining(@PathVariable int datasetId) {
        String response = modelService.launchTraining(datasetId);
        return GlobalSuccessHandler.success(response);
    }

    @PostMapping("/test/{datasetId}")
    public ResponseEntity<?> launchTesting(@PathVariable int datasetId) {
        String response = modelService.launchTesting(datasetId);
        return GlobalSuccessHandler.success(response);
    }

    @GetMapping("/train/history/{datasetId}")
    public ResponseEntity<?> getTrainingHistory(@PathVariable int datasetId) {
        String response = modelService.getTrainingHistory(datasetId);
        return GlobalSuccessHandler.success(response);
    }

    @GetMapping("/test/history/{datasetId}")
    public ResponseEntity<?> getTestingHistory(@PathVariable int datasetId) {
        String response = modelService.getTestingHistory(datasetId);
        return GlobalSuccessHandler.success(response);
    }

    @PostMapping("/logs/receive")
    public void receiveLog(@RequestBody String log) {
        messagingTemplate.convertAndSend("/topic/logs", log);
    }
}
