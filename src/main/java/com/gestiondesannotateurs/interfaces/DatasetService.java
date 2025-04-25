package com.gestiondesannotateurs.intefaces;

import com.gestiondesannotateurs.dtos.DatasetUploadRequest;
import com.gestiondesannotateurs.entities.Dataset;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

public interface DatasetService {
    Dataset createDataset(DatasetUploadRequest dataset) throws CsvValidationException, IOException;
}
