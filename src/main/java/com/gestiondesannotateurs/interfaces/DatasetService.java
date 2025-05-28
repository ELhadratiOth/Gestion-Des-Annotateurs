package com.gestiondesannotateurs.interfaces;


import com.gestiondesannotateurs.dtos.*;
import com.gestiondesannotateurs.entities.Dataset;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.List;

public interface DatasetService {
    Dataset createDataset(DatasetUploadRequest dataset) throws CsvValidationException, IOException;
    void deleteDataset(Long idDataset);
    List<DatasetInfo> getAll();
    Dataset updateDataset(DatasetUpdata  datasetUpdata , Long idDataset);
    DatasetInfoTask taskInfo(Long idDataset);
    List<Dataset> getTerminatedAnnotatedDatasets();
    List<Dataset> getNotTerminatedAnnotatedDatasets();
    List<Dataset> getAssignedDatasets();
    List<Dataset> getNotAssignedDatasets();
    double calculateDatasetAdvacement(Long datasetId);

    void updateDatasetAdvancement(Long datasetId);

    Dataset findDatasetById(Long idDataset);
    ResponseEntity<Resource> downloadFileByDatasetId(Long datasetId) throws IOException;
    LastFinishedDataset lastCompletedTask();

}
