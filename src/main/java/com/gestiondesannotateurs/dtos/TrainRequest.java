package com.gestiondesannotateurs.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class TrainRequest {

    @NotNull
    private MultipartFile file;
    @NotBlank(message = "Please precise the task similarity/infrence")
    private String task;
    private double learningRate;
    private int epochs;
    private int batchSize;
    private String user;
    private int datasetId;

    public TrainRequest(MultipartFile file, String task, double learningRate, int epochs, int batchSize, String user, int datasetId) {
        this.file = file;
        this.task = task;
        this.learningRate = learningRate;
        this.epochs = epochs;
        this.batchSize = batchSize;
        this.user = user;
        this.datasetId = datasetId;
    }

    public MultipartFile getFile() { return file; }
    public String getTask() { return task; }
    public double getLearningRate() { return learningRate; }
    public int getEpochs() { return epochs; }
    public int getBatchSize() { return batchSize; }
    public String getUser() { return user; }
    public int getDatasetId() { return datasetId; }
}

