package com.gestiondesannotateurs.services;

import com.gestiondesannotateurs.dtos.DatasetUploadRequest;
import com.gestiondesannotateurs.entities.Dataset;
import com.gestiondesannotateurs.entities.Label;
import com.gestiondesannotateurs.intefaces.CoupleOfTextService;
import com.gestiondesannotateurs.intefaces.DatasetService;
import com.gestiondesannotateurs.repositories.DatasetRepo;
import com.gestiondesannotateurs.repositories.LabelRepo;
import com.opencsv.exceptions.CsvValidationException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class DatasetServiceImpl implements DatasetService {

    @Autowired
    private DatasetRepo datasetRepo;

    @Autowired
    private LabelRepo labelRepo;

    @Autowired
    private CoupleOfTextService coupleOfTextService;

    @Transactional
    public Dataset createDataset(DatasetUploadRequest dataset) throws CsvValidationException, IOException {
        Label label = labelRepo.findById(dataset.labelId())
                .orElseThrow(() -> new IllegalArgumentException("Label with ID " + dataset.labelId() + " not found"));

        Dataset datasetEntity = new Dataset();
        datasetEntity.setName(dataset.name());
        datasetEntity.setDescription(dataset.description());
        datasetEntity.setLabel(label);
        Dataset createdDataset = datasetRepo.save(datasetEntity);

        Long rowNumber =  coupleOfTextService.createRows(createdDataset, dataset.file());
        createdDataset.setSize(rowNumber);
        datasetRepo.save(createdDataset);


        return createdDataset;
    }
}
