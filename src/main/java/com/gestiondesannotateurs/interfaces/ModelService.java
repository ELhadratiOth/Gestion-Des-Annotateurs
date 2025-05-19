package com.gestiondesannotateurs.interfaces;

import com.gestiondesannotateurs.dtos.TrainRequest;

import java.io.IOException;

public interface ModelService {
    String uploadFullDataset(TrainRequest request) throws IOException;
    String launchTraining(int datasetId);
    String launchTesting(int datasetId);
    String getTrainingHistory(int datasetId);
    String getTestingHistory(int datasetId);
}
