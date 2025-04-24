package com.gestiondesannotateurs.controllers;


import com.gestiondesannotateurs.dtos.DatasetCreate;
import com.gestiondesannotateurs.entities.Dataset;
import com.gestiondesannotateurs.interfaces.DatasetService;
import com.gestiondesannotateurs.repositories.TaskToDoRepo;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/datasets")
public class DatasetController {

    @Autowired
    private DatasetService datasetService;


    @Autowired
    private TaskToDoRepo taskToDoRepo;

    private static final String CSV_CONTENT_TYPE = "text/csv";
    private static final String JSON_CONTENT_TYPE = "application/json";

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<?> createDataset(
            @RequestPart("file") MultipartFile file,
            @RequestPart("dataset") @Valid DatasetCreate dataset
    ) {

        // Validate file presence
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST ,  "file  is empty" );
        }

        // Validate file type
        String contentType = file.getContentType();
        if (!CSV_CONTENT_TYPE.equals(contentType) && !JSON_CONTENT_TYPE.equals(contentType)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST , "Invalid file type. Only CSV or JSON files are allowed.");
        }

        // Create Dataset using DatasetService
        Dataset createdDataset = datasetService.createDataset(dataset);

        return new ResponseEntity<>(createdDataset, HttpStatus.CREATED);

    }


}
