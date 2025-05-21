package com.gestiondesannotateurs.services;

import com.gestiondesannotateurs.dtos.DatasetInfo;
import com.gestiondesannotateurs.dtos.DatasetUpdata;
import com.gestiondesannotateurs.dtos.DatasetUploadRequest;
import com.gestiondesannotateurs.entities.*;
import com.gestiondesannotateurs.interfaces.CoupleOfTextService;
import com.gestiondesannotateurs.interfaces.DatasetService;
import com.gestiondesannotateurs.interfaces.TaskService;
import com.gestiondesannotateurs.repositories.CoupleOfTextRepo;
import com.gestiondesannotateurs.repositories.DatasetRepo;
import com.gestiondesannotateurs.repositories.LabelRepo;
import com.gestiondesannotateurs.repositories.TaskToDoRepo;
import com.gestiondesannotateurs.shared.Exceptions.CustomResponseException;
import com.opencsv.exceptions.CsvValidationException;
import jakarta.transaction.Transactional;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class DatasetServiceImpl implements DatasetService {

    @Autowired
    private DatasetRepo datasetRepo;

    @Autowired
    private LabelRepo labelRepo;

    @Autowired
    private CoupleOfTextService coupleOfTextService;

    @Autowired
    private CoupleOfTextRepo coupletextRepo;

    @Autowired
    private TaskToDoRepo taskToDoRepo;

    @Value("${file.upload-dir}")
    private String uploadDir;




    @Override
    public Dataset createDataset(DatasetUploadRequest dataset) throws CsvValidationException, IOException {
        Label label = labelRepo.findById(dataset.labelId())
                .orElseThrow(() -> new IllegalArgumentException("Label with ID " + dataset.labelId() + " not found"));
//        System.out.println("bugg1");

        Dataset datasetEntity = new Dataset();
        datasetEntity.setName(dataset.name());
        datasetEntity.setDescription(dataset.description());
        datasetEntity.setLabel(label);
        datasetEntity.setFilePath(uploadDir+dataset.file().getOriginalFilename());
        Dataset createdDataset = datasetRepo.save(datasetEntity);
//System.out.println("bugg2");
        Long rowNumber = coupleOfTextService.createRows(createdDataset, dataset.file());
//        System.out.println("buggyyyy");

        createdDataset.setSize(rowNumber);
//        System.out.println("bugg3");

        datasetRepo.save(createdDataset);

        return createdDataset;
    }

    @Override
    public void deleteDataset(Long datasetId) {
        // Validate dataset
        Dataset dataset = datasetRepo.findById(datasetId)
                .orElseThrow(() -> new CustomResponseException(404, "Dataset with ID: " + datasetId + " not found"));

        // Fetch all Coupletext entities for the dataset
        List<Coupletext> coupletexts = coupletextRepo.findByDataset(dataset);

//        // Delete AnnotationClass entities linked to each Coupletext
//        for (Coupletext coupletext : coupletexts) {
//            List<AnnotationClass> annotations = annotationClassRepo.findByCoupletext(coupletext);
//            annotationClassRepo.deleteAll(annotations);
//        }

        // Delete coupletext_task associations by clearing tasks from Coupletext
        for (Coupletext coupletext : coupletexts) {
            coupletext.getTasks().clear(); // Removes all entries in coupletext_task for this Coupletext
            coupletextRepo.save(coupletext);
        }

        // Delete all Coupletext entities
        coupletextRepo.deleteAll(coupletexts);

        // Delete all TaskToDo entities for the dataset
        List<TaskToDo> tasks = taskToDoRepo.findByDataset(dataset);
        taskToDoRepo.deleteAll(tasks);

        // Delete the Dataset
        datasetRepo.delete(dataset);
    }

    @Override
    public List<DatasetInfo> getAll() {
        return datasetRepo.findAll().stream()
                .map(dataset -> {
                    Optional<Label> label = labelRepo.findById(dataset.getLabel().getId());
                    if(label.isEmpty()) {
                        throw new CustomResponseException(400,"Label with ID " + dataset.getLabel() + " not found");
                    }
                    return new DatasetInfo(
                            dataset.getId(),
                            dataset.getSize(),
                            dataset.getName(),
                            dataset.getAdvancement(),
                            dataset.getDescription(),
                            label.get().getName(),
                            dataset.getCreatedAt()
                    );
                })
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public Dataset updateDataset(DatasetUpdata datasetUpdata, Long idDataset) {
        Optional<Dataset> dataset = datasetRepo.findById(idDataset);
        if (dataset.isEmpty()) {
            throw new CustomResponseException(400, "Dataset with ID " + idDataset + " not found");
        }
        Optional<Label> label = labelRepo.findById(datasetUpdata.labelId());
        if (label.isEmpty()) {
            throw new CustomResponseException(400, "Label with ID " + datasetUpdata.labelId() + " not found");
        }

        Dataset datasetEntity = dataset.get();
        datasetEntity.setName(datasetUpdata.name());
        datasetEntity.setDescription(datasetUpdata.description());
        datasetEntity.setLabel(label.get());

        // Save the entity
        Dataset savedDataset = datasetRepo.save(datasetEntity);

        // Ensure the label is initialized within the transaction
        Hibernate.initialize(savedDataset.getLabel());

        return savedDataset;
    }

    @Override
    public DatasetInfo taskInfo(Long idDataset) {
        Optional<Dataset> dataset = datasetRepo.findById(idDataset);
        if(dataset.isEmpty()){
            throw new CustomResponseException(404,"Dataset doesnt exist with this id");
        }

        Optional<Label> label = labelRepo.findById(dataset.get().getLabel().getId());
        if(label.isEmpty()){
            throw new CustomResponseException(404,"Label doesnt exist");
        }

        return new DatasetInfo(
                dataset.get().getId(),
                dataset.get().getSize(),
                dataset.get().getName(),
                dataset.get().getAdvancement(),
                dataset.get().getDescription(),
                label.get().getName(),
                dataset.get().getCreatedAt()
        );
    }


    @Override
    public List<Dataset> getTerminatedAnnotatedDatasets() {
        List<Dataset> datasets =  datasetRepo.findAllWithAdvancement100();
        return datasets;
    }

    @Override
    public List<Dataset> getNotTerminatedAnnotatedDatasets() {
        List<Dataset> datasets =  datasetRepo.findAllWithAdvancementNot100();
        return datasets;
    }

    @Override
    public Dataset findDatasetById(Long idDataset) {
        return datasetRepo.findById(idDataset)
                .orElseThrow(() -> new CustomResponseException(404, "Dataset with ID " + idDataset + " not found"));
    }
}