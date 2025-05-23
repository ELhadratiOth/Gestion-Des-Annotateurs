package com.gestiondesannotateurs.services;

import com.gestiondesannotateurs.dtos.LabelCreate;
import com.gestiondesannotateurs.dtos.LabelResponse;
import com.gestiondesannotateurs.entities.Dataset;
import com.gestiondesannotateurs.entities.Label;
import com.gestiondesannotateurs.interfaces.DatasetService;
import com.gestiondesannotateurs.interfaces.LabelService;
import com.gestiondesannotateurs.repositories.LabelRepo;
import com.gestiondesannotateurs.shared.Exceptions.CustomResponseException;
import com.gestiondesannotateurs.shared.Exceptions.GlobalSuccessHandler;
import com.gestiondesannotateurs.shared.GlobalResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

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

        newLabel.setClasses(label.classes().toLowerCase());

        return labelRepo.save(newLabel);
    }

    @Override
    public List<LabelResponse> getAll() {
        List<Label> labels = labelRepo.findAllByDeletedFalse();
        List<LabelResponse> responses = new ArrayList<>();
        for (Label label : labels) {
            LabelResponse labelResp = new LabelResponse(label.getId(), label.getName(), label.getClasses());
            responses.add(labelResp);
        }
        return responses;
    }

    @Override
    public void deleteLabel(Long labelId) {
        Optional<Label> label = labelRepo.findById(labelId);
        if(label.isEmpty()){
            throw new CustomResponseException(404, "Label not found");
        }
        Label l = label.get();
        l.setDeleted(true);
        labelRepo.save(l);
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
