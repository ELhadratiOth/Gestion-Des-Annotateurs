package com.gestiondesannotateurs.controllers;
import com.gestiondesannotateurs.dtos.DatasetInfo;
import com.gestiondesannotateurs.dtos.DatasetUpdata;
import com.gestiondesannotateurs.dtos.DatasetUploadRequest;
import com.gestiondesannotateurs.entities.Dataset;
import com.gestiondesannotateurs.interfaces.DatasetService;
import com.gestiondesannotateurs.repositories.TaskToDoRepo;
import com.gestiondesannotateurs.shared.Exceptions.GlobalSuccessHandler;
import com.gestiondesannotateurs.shared.GlobalResponse;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
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
    public ResponseEntity<GlobalResponse<DatasetInfo>> infoDataset(@PathVariable Long idDataset) {
        DatasetInfo datasetInfo = datasetService.taskInfo(idDataset);
        return GlobalSuccessHandler.success(datasetInfo);
    }

    @PostMapping()
    public ResponseEntity<GlobalResponse<Map<String, Object>>> createDataset(
            @ModelAttribute DatasetUploadRequest dataset) throws CsvValidationException, IOException {

        Dataset createdDataset = datasetService.createDataset(dataset);
        return GlobalSuccessHandler.createdWithId(
                "Dataset created successfully",
                createdDataset.getId(),
                createdDataset
        );
    }

    @DeleteMapping("/{idDataset}")
    public ResponseEntity<GlobalResponse<String>> deleteDataset(@PathVariable Long idDataset) {
        datasetService.deleteDataset(idDataset);
        return GlobalSuccessHandler.deleted("Dataset supprimé avec succès");
    }

    @GetMapping
    public ResponseEntity<GlobalResponse<List<DatasetInfo>>> getAllDatasets() {
        List<DatasetInfo> datasets = datasetService.getAll();
        return GlobalSuccessHandler.success("Liste des datasets récupérée avec succès", datasets);
    }

    @PutMapping("/{idDataset}")
    public ResponseEntity<GlobalResponse<Dataset>> updateDataset(
            @RequestBody DatasetUpdata updataDataset,
            @PathVariable Long idDataset) {
        Dataset updatedDataset = datasetService.updateDataset(updataDataset, idDataset);
        return GlobalSuccessHandler.success("Dataset mis à jour avec succès", updatedDataset);
    }
}