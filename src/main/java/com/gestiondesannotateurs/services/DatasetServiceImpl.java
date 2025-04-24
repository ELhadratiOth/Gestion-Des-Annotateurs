package com.gestiondesannotateurs.services;

import com.gestiondesannotateurs.dtos.DatasetCreate;
import com.gestiondesannotateurs.entities.Dataset;
import com.gestiondesannotateurs.entities.Label;
import com.gestiondesannotateurs.intefaces.DatasetService;
import com.gestiondesannotateurs.repositories.DatasetRepo;
import com.gestiondesannotateurs.repositories.LabelRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DatasetServiceImpl implements DatasetService {

    @Autowired
    private DatasetRepo datasetRepo;

    @Autowired
    private LabelRepo labelRepo;

    public Dataset createDataset(DatasetCreate dataset) {
        Label label = labelRepo.findById(dataset.labelId())
                .orElseThrow(() -> new IllegalArgumentException("Label with ID " + dataset.labelId() + " not found"));

        Dataset datasetEntity = new Dataset();
        datasetEntity.setName(dataset.name());
        datasetEntity.setDescription(dataset.description());
        datasetEntity.setLabel(label);

        return datasetRepo.save(datasetEntity);

    }
}
