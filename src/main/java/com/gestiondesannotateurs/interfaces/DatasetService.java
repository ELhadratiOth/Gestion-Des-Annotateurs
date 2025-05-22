package com.gestiondesannotateurs.interfaces;


import com.gestiondesannotateurs.dtos.DatasetInfo;
import com.gestiondesannotateurs.dtos.DatasetUpdata;
import com.gestiondesannotateurs.dtos.DatasetUploadRequest;
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
    DatasetInfo taskInfo(Long idDataset);
    List<Dataset> getTerminatedAnnotatedDatasets();
    List<Dataset> getNotTerminatedAnnotatedDatasets();

    double calculateDatasetAdvacement(Long datasetId);

    void updateDatasetAdvancement(Long datasetId);

    Dataset findDatasetById(Long idDataset);
    ResponseEntity<Resource> downloadFileByDatasetId(Long datasetId) throws IOException;
}
