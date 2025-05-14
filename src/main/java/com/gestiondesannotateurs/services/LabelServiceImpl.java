package com.gestiondesannotateurs.services;

import com.gestiondesannotateurs.dtos.LabelCreate;
import com.gestiondesannotateurs.dtos.LabelResponse;
import com.gestiondesannotateurs.entities.Dataset;
import com.gestiondesannotateurs.entities.Label;
import com.gestiondesannotateurs.interfaces.DatasetService;
import com.gestiondesannotateurs.interfaces.LabelService;
import com.gestiondesannotateurs.repositories.LabelRepo;
import com.gestiondesannotateurs.shared.Exceptions.CustomResponseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class LabelServiceImpl implements LabelService {

    @Autowired
    private LabelRepo labelRepo;

    @Autowired
    private DatasetService datasetService;


    @Override
    public Label createLabel(LabelCreate label) {
        Label newLabel = new Label() ;
        newLabel.setName(label.name().trim());

        return labelRepo.save(newLabel);
    }

    @Override
    public List<LabelResponse> getAll() {
        List<Label> labels = labelRepo.findAll();
        List<LabelResponse> responses = new ArrayList<>();
        for (Label label : labels) {
            LabelResponse labelResp = new LabelResponse(label.getId(), label.getName());
            responses.add(labelResp);
        }
        return responses;
    }

//    @Override
//    public Label findByDataset(Long datasetsId) {
//        Optional<Label> label =  labelRepo.findByDataset(datasetService.findDatasetById(datasetsId));
//        if(label.isPresent()){
//            return label.get();
//        }
//        throw new CustomResponseException(404, "Label not found");
//    }
}
