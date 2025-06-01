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
    @NotBlank(message = "Please specify your project name")
    private String projectName;
    private double learningRate;
    private int epochs;
    private int batchSize;
    private String user;

    public TrainRequest(MultipartFile file, String task, String projectName,double learningRate, int epochs, int batchSize, String user) {
        this.file = file;
        this.task = task;
        this.projectName = projectName;
        this.learningRate = learningRate;
        this.epochs = epochs;
        this.batchSize = batchSize;
        this.user = user;
    }

    public MultipartFile getFile() { return file; }
    public String getTask() { return task; }
    public String getProjectName() { return projectName; }
    public double getLearningRate() { return learningRate; }
    public int getEpochs() { return epochs; }
    public int getBatchSize() { return batchSize; }
    public String getUser() { return user; }

}

