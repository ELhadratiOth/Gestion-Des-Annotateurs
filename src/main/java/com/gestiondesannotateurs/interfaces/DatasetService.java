package com.gestiondesannotateurs.interfaces;


import com.gestiondesannotateurs.dtos.DatasetInfo;
import com.gestiondesannotateurs.dtos.DatasetUpdata;
import com.gestiondesannotateurs.dtos.DatasetUploadRequest;
import com.gestiondesannotateurs.entities.Dataset;
import com.opencsv.exceptions.CsvValidationException;

import java.io.IOException;
import java.util.List;

public interface DatasetService {
    Dataset createDataset(DatasetUploadRequest dataset) throws CsvValidationException, IOException;
    void deleteDataset(Long idDataset);
    List<DatasetInfo> getAll();
    Dataset updateDataset(DatasetUpdata  datasetUpdata , Long idDataset);
    DatasetInfo taskInfo(Long idTask);

    Dataset findDatasetById(Long idDataset);
}
