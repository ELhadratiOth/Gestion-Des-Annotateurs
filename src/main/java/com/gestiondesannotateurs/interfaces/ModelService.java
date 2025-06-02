package com.gestiondesannotateurs.interfaces;

import com.gestiondesannotateurs.dtos.TrainRequest;

import java.io.IOException;

public interface ModelService {
    String uploadFullDataset(TrainRequest request) throws IOException;
    String launchTraining(int datasetId,String projectName);
    String launchTesting(int datasetId,String projectName);
    String getTrainingHistory(int datasetId,String projectName);
    String getTestingHistory(int datasetId,String projectName);
    String getHistory();
}
