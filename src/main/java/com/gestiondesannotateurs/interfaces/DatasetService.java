package com.gestiondesannotateurs.interfaces;


import com.gestiondesannotateurs.dtos.DatasetUploadRequest;
import com.gestiondesannotateurs.entities.Dataset;
import com.opencsv.exceptions.CsvValidationException;

import java.io.IOException;
import java.util.List;

public interface DatasetService {
    Dataset createDataset(DatasetUploadRequest dataset) throws CsvValidationException, IOException;
    void deleteDataset(Long idDataset);
    List<Dataset> getAll();
}
