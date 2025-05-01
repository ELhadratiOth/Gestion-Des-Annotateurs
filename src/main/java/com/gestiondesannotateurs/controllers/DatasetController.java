package com.gestiondesannotateurs.controllers;


import com.gestiondesannotateurs.dtos.DatasetInfo;
import com.gestiondesannotateurs.dtos.DatasetUpdata;
import com.gestiondesannotateurs.dtos.DatasetUploadRequest;
import com.gestiondesannotateurs.entities.Dataset;
import com.gestiondesannotateurs.interfaces.CoupleOfTextService;
import com.gestiondesannotateurs.interfaces.DatasetService;
import com.gestiondesannotateurs.repositories.TaskToDoRepo;
import com.gestiondesannotateurs.shared.Exceptions.AnnotatorNotFoundException;
import com.gestiondesannotateurs.shared.GlobalResponse;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/datasets")
public class DatasetController {

    public static final String CSV_CONTENT_TYPE = "text/csv";
    public static final String JSON_CONTENT_TYPE = "application/json";

    @Autowired
    private DatasetService datasetService;


    @Autowired
    private TaskToDoRepo taskToDoRepo;


    @GetMapping("/{idDataset}")
    public ResponseEntity<?> infoDataset(@PathVariable Long idDataset) {

        return new ResponseEntity<>(datasetService.taskInfo(idDataset), HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<?> createDataset(@ModelAttribute  DatasetUploadRequest dataset) throws CsvValidationException, IOException {


        Map<String, Object> response = new HashMap<>();
        Dataset createdDataset = datasetService.createDataset(dataset);
        response.put("message", "Dataset created successfully");
        response.put("datasetId", createdDataset.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{idDataset}")
    public ResponseEntity<?> deleteDataset(@PathVariable Long idDataset){
        datasetService.deleteDataset(idDataset);

        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<?> getAllDatasets(){
        System.out.println("get all ");
        List<DatasetInfo> datasets =  datasetService.getAll();
        return new ResponseEntity<>(GlobalResponse.success(datasets), HttpStatus.OK);
    }
    @PutMapping("/{idDataset}")
    public ResponseEntity<?> updateDataset(@RequestBody DatasetUpdata updataDataset , @PathVariable Long idDataset)  {

        Dataset updatedDataset = datasetService.updateDataset(updataDataset , idDataset);
        return new ResponseEntity<>(GlobalResponse.success(updatedDataset), HttpStatus.OK);

    }


}
