package com.gestiondesannotateurs.interfaces;

import com.gestiondesannotateurs.dtos.DatasetCreate;
import com.gestiondesannotateurs.entities.Dataset;

public interface DatasetService {
    Dataset createDataset(DatasetCreate dataset);
}
