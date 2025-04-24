package com.gestiondesannotateurs.intefaces;

import com.gestiondesannotateurs.dtos.DatasetCreate;
import com.gestiondesannotateurs.entities.Dataset;
import org.springframework.http.ResponseEntity;

public interface DatasetService {
    Dataset createDataset(DatasetCreate dataset);
}
