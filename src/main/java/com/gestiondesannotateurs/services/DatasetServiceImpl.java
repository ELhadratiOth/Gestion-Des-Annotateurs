package com.gestiondesannotateurs.services;

import com.gestiondesannotateurs.dtos.DatasetUploadRequest;

import com.gestiondesannotateurs.entities.Dataset;
import com.gestiondesannotateurs.entities.Label;
import com.gestiondesannotateurs.interfaces.CoupleOfTextService;
import com.gestiondesannotateurs.interfaces.DatasetService;
import com.gestiondesannotateurs.repositories.DatasetRepo;
import com.gestiondesannotateurs.repositories.LabelRepo;
import com.gestiondesannotateurs.shared.Exceptions.CustomResponseException;
import com.opencsv.exceptions.CsvValidationException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

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

    @Override
    public void deleteDataset(Long idDataset) {
        Optional<Dataset> dataset = datasetRepo.findById(idDataset);
        if(dataset.isEmpty()) {
            throw new CustomResponseException(400,"Dataset with ID " + idDataset + " not found");
        }
        datasetRepo.deleteById(idDataset);
    }

    @Override
    public List<Dataset> getAll() {
        List<Dataset> datasets = datasetRepo.findAll();

        return datasets;
    }
}
