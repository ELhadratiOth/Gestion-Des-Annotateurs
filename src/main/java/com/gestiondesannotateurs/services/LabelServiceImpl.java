package com.gestiondesannotateurs.services;

import com.gestiondesannotateurs.dtos.LabelCreate;
import com.gestiondesannotateurs.dtos.LabelResponse;
import com.gestiondesannotateurs.entities.Label;
import com.gestiondesannotateurs.interfaces.LabelService;
import com.gestiondesannotateurs.repositories.LabelRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LabelServiceImpl implements LabelService {

    @Autowired
    private LabelRepo labelRepo;


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
}
