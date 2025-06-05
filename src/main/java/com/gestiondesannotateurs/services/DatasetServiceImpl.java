package com.gestiondesannotateurs.services;

import com.gestiondesannotateurs.dtos.*;
import com.gestiondesannotateurs.entities.*;
import com.gestiondesannotateurs.interfaces.AnnotatorService;
import com.gestiondesannotateurs.interfaces.CoupleOfTextService;
import com.gestiondesannotateurs.interfaces.DatasetService;
import com.gestiondesannotateurs.interfaces.TaskService;
import com.gestiondesannotateurs.repositories.*;
import com.gestiondesannotateurs.shared.Exceptions.CustomResponseException;
import com.gestiondesannotateurs.shared.Exceptions.GlobalSuccessHandler;
import com.gestiondesannotateurs.shared.GlobalResponse;
import com.gestiondesannotateurs.utils.ProcessFile;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import jakarta.transaction.Transactional;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.xml.crypto.Data;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
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

    @Autowired
    TaskService taskService;



    @Autowired
    AnnotatorRepo annotatorRepo;

    @Value("${file.upload-dir}")
    private String uploadDir;
    @Autowired
    private AdminServiceImpl adminServiceImpl;


    @Override
    public Dataset createDataset(DatasetUploadRequest dataset) {
        try {
            Label label = labelRepo.findById(dataset.labelId())
                    .orElseThrow(() -> new IllegalArgumentException("Label with ID " + dataset.labelId() + " not found"));
//        System.out.println("bugg1");

            Dataset datasetEntity = new Dataset();
            datasetEntity.setName(dataset.name());
            datasetEntity.setDescription(dataset.description());
            datasetEntity.setLabel(label);
            datasetEntity.setSizeMB(dataset.sizeMB());
            Dataset createdDataset = datasetRepo.save(datasetEntity);
//System.out.println("bugg2");
            Long rowNumber = coupleOfTextService.createRows(createdDataset, dataset.file());
//        System.out.println("buggyyyy");

            createdDataset.setSize(rowNumber);
//        System.out.println("bugg3");

            datasetRepo.save(createdDataset);

            return createdDataset;
        }
        catch (Exception e){
            throw new CustomResponseException(400,e.getMessage());
        }

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
                    System.out.println("date assigne :"+dataset.getAffectationDate());
                    Optional<Label> label = labelRepo.findById(dataset.getLabel().getId());
                    if(label.isEmpty()) {
                        throw new CustomResponseException(400,"Label with ID " + dataset.getLabel() + " not found");
                    }

                    List<Annotator> annotators = new ArrayList<>() ;

                    List<TaskToDoDto> tasks =  taskService.getTasksByDatasetId(dataset.getId());
                    for (TaskToDoDto task : tasks){
                        Optional<Annotator> annotator = annotatorRepo.getAnnotatorByTask(task.id());
                        if(annotator.isPresent()){
                            annotators.add(annotator.get());
                        }else{
                            throw new CustomResponseException(400,"Annotator with ID " + task.annotatorId() + " not found");
                        }
                    }


                    return new DatasetInfo(
                            dataset.getId(),
                            dataset.getSize(),
                            dataset.getSizeMB(),
                            dataset.getName(),
                            dataset.getAdvancement(),
                            dataset.getDescription(),
                            label.get().getName(),
                            dataset.getCreatedAt(),
                            dataset.getIsAssigned(),
                            annotators,
                            dataset.getAffectationDate()

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
    public double calculateDatasetAdvacement(Long datasetId){
        Optional<Dataset> dataset = datasetRepo.findById(datasetId);
        if(dataset.isEmpty()){
            throw new CustomResponseException(404,"Dataset doesnt exist with this id");
        }

         Long totalAdavement = coupletextRepo.countByDatasetIdAndTrueLabelNot(datasetId,"NOT_YET");
        double ratio = (double) totalAdavement / dataset.get().getSize();
        System.out.println("My ratio :");

        System.out.println(ratio);
        return Math.round(ratio * 100.0 * 100.0) / 100.0;
    }
    @Override
    public void updateDatasetAdvancement(Long datasetId) {
        double advancement = calculateDatasetAdvacement(datasetId);
        Dataset dataset = datasetRepo.findById(datasetId)
                .orElseThrow(() -> new CustomResponseException(404, "Dataset not found"));

        dataset.setAdvancement(advancement);
        datasetRepo.save(dataset);
    }


    @Override
    public DatasetInfoTask taskInfo(Long idDataset) {
        Optional<Dataset> dataset = datasetRepo.findById(idDataset);
        if(dataset.isEmpty()){
            throw new CustomResponseException(404,"Dataset doesnt exist with this id");
        }

        Optional<Label> label = labelRepo.findById(dataset.get().getLabel().getId());
        if(label.isEmpty()){
            throw new CustomResponseException(404,"Label doesnt exist");
        }

        return new DatasetInfoTask(
                dataset.get().getId(),
                dataset.get().getSize(),
                dataset.get().getSizeMB(),
                dataset.get().getName(),
                dataset.get().getAdvancement(),
                dataset.get().getDescription(),
                label.get().getName(),
                dataset.get().getCreatedAt(),
                dataset.get().getIsAssigned()
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
    public List<Dataset> getAssignedDatasets() {
        return datasetRepo.findAll().stream()
                .filter(dataset -> Boolean.TRUE.equals(dataset.getIsAssigned()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Dataset> getNotAssignedDatasets() {
        return datasetRepo.findAll().stream()
                .filter(dataset -> !(Boolean.TRUE.equals(dataset.getIsAssigned())))
                .collect(Collectors.toList());
    }

    @Override
    public Dataset findDatasetById(Long idDataset) {
        return datasetRepo.findById(idDataset)
                .orElseThrow(() -> new CustomResponseException(404, "Dataset with ID " + idDataset + " not found"));
    }

    public ResponseEntity downloadFileByDatasetId(Long datasetId)  {

        // Verify dataset exists
        Dataset dataset = findDatasetById(datasetId);

        // verif  the  advancement

        if(dataset.getAdvancement() != 100.0){
            return   new ResponseEntity<>("Dataset with ID " + datasetId + " not finished yet" , HttpStatus.NOT_FOUND);
        }

        // Fetch admin-annotated CoupleOfTextWithAnnotation data
        List<Coupletext> coupleOfTextWithAnnotations = coupletextRepo.findAllByDataset(dataset) ;
        if (coupleOfTextWithAnnotations.isEmpty()) {
            throw new CustomResponseException(404, "no couple pf  text existing " + datasetId);
        }
        // Generate CSV
        StringWriter stringWriter = new StringWriter();
        try (CSVWriter csvWriter = new CSVWriter(stringWriter)) {
            String[] header = { "textA", "textB", "annotation"};
            csvWriter.writeNext(header);

            for (Coupletext record : coupleOfTextWithAnnotations) {
                String[] row = {

                        record.getTextA() != null ? record.getTextA() : "",
                        record.getTextB() != null ? record.getTextB(): "",
                        record.getTrueLabel() != null ? record.getTrueLabel() : ""
                };
                csvWriter.writeNext(row);
            }
        } catch (Exception e) {
            throw new CustomResponseException(500, "Failed to generate CSV: " + e.getMessage());
        }

        // Prepare file content
        byte[] csvBytes = stringWriter.toString().getBytes();
        ByteArrayResource resource = new ByteArrayResource(csvBytes);
        // Set HTTP headers
        HttpHeaders headers = new HttpHeaders();
        String downloadFileName = "dataset_" + datasetId + ".csv";
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + downloadFileName);
        headers.add(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
        headers.add(HttpHeaders.PRAGMA, "no-cache");
        headers.add(HttpHeaders.EXPIRES, "0");

        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    }



    @Override
    public LastFinishedDataset lastCompletedTask() {
        Optional<Dataset> dataset = datasetRepo.findLastAddedDataset();
        if(dataset.isEmpty()){
            return  null;
        }
        return new LastFinishedDataset(
                dataset.get().getName(),
                dataset.get().getCreatedAt(),
                dataset.get().getSizeMB(),
                dataset.get().getLabel().getName()
        );
    }


}