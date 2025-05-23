package com.gestiondesannotateurs.controllers;
import com.gestiondesannotateurs.dtos.DatasetInfo;
import com.gestiondesannotateurs.dtos.DatasetInfoTask;
import com.gestiondesannotateurs.dtos.DatasetUpdata;
import com.gestiondesannotateurs.dtos.DatasetUploadRequest;
import com.gestiondesannotateurs.entities.Dataset;
import com.gestiondesannotateurs.interfaces.DatasetService;
import com.gestiondesannotateurs.repositories.TaskToDoRepo;
import com.gestiondesannotateurs.shared.Exceptions.CustomResponseException;
import com.gestiondesannotateurs.shared.Exceptions.GlobalSuccessHandler;
import com.gestiondesannotateurs.shared.GlobalResponse;
import com.gestiondesannotateurs.utils.ProcessFile;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/datasets")
public class DatasetController {

    public static final String CSV_CONTENT_TYPE = "text/csv";
    public static final String JSON_CONTENT_TYPE = "application/json";

    @Autowired
    private DatasetService datasetService;



    @GetMapping("/{idDataset}")
    @PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN', 'ROLE_ADMIN','ROLE_ANNOTATOR')")

    public ResponseEntity<GlobalResponse<DatasetInfoTask>> infoDataset(@PathVariable Long idDataset) {
        DatasetInfoTask datasetInfo = datasetService.taskInfo(idDataset);
        return GlobalSuccessHandler.success(datasetInfo);
    }

    @PostMapping()
    @PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN', 'ROLE_ADMIN')")
    public ResponseEntity<?> createDataset(
            @ModelAttribute DatasetUploadRequest dataset) throws CsvValidationException, IOException {

        Dataset createdDataset = datasetService.createDataset(dataset);
        return GlobalSuccessHandler.created(
                "Dataset created successfully",
                createdDataset
        );
    }

    @DeleteMapping("/{idDataset}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ROLE_ADMIN')")

    public ResponseEntity<GlobalResponse<String>> deleteDataset(@PathVariable Long idDataset) {
        datasetService.deleteDataset(idDataset);
        return GlobalSuccessHandler.deleted("Dataset supprimé avec succès");
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<GlobalResponse<List<DatasetInfo>>> getAllDatasets() {
        System.out.println("getAllDatasets");
        List<DatasetInfo> datasets = datasetService.getAll();
        return GlobalSuccessHandler.success("Liste des datasets récupérée avec succès", datasets);
    }

    @PutMapping("/{idDataset}")
    @PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN', 'ROLE_ADMIN')")
    public ResponseEntity<GlobalResponse<Dataset>> updateDataset(
            @RequestBody DatasetUpdata updataDataset,
            @PathVariable Long idDataset) {
        Dataset updatedDataset = datasetService.updateDataset(updataDataset, idDataset);
        return GlobalSuccessHandler.success("Dataset mis à jour avec succès", updatedDataset);
    }

    @GetMapping("/advancement/1")
    @PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN', 'ROLE_ADMIN')")

    public ResponseEntity<GlobalResponse<List<Dataset>>> getTerminatedAnnotatedDatasets(){
        List<Dataset> datasets =  datasetService.getTerminatedAnnotatedDatasets();

        return GlobalSuccessHandler.success("List of all the datasets that are 100% annotated " , datasets);
    }

    @GetMapping("/advancement/0")
    @PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN', 'ROLE_ADMIN')")

    public ResponseEntity<GlobalResponse<List<Dataset>>> getNotTerminatedAnnotatedDatasets(){
        List<Dataset> datasets =  datasetService.getNotTerminatedAnnotatedDatasets();
        return GlobalSuccessHandler.success("List of all the datasets that are not 100% annotated " , datasets);
    }

    @GetMapping("/download/{datasetId}")
    @PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN', 'ROLE_ADMIN','ROLE_ANNOTATOR')")
    public ResponseEntity<?> downloadFileByDatasetId(@PathVariable Long datasetId) throws IOException {
        return datasetService.downloadFileByDatasetId(datasetId);
    }



}