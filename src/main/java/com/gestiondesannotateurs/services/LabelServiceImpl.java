package com.gestiondesannotateurs.services;

import com.gestiondesannotateurs.dtos.LabelCreate;
import com.gestiondesannotateurs.entities.Label;
import com.gestiondesannotateurs.interfaces.LabelService;
import com.gestiondesannotateurs.repositories.LabelRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LabelServiceImpl implements LabelService {

    @Autowired
    private LabelRepo labelRepo;


    @Override
    public Label createLabel(LabelCreate label) {

        Label newLabel = new Label() ;
        newLabel.setName(label.name());

        return labelRepo.save(newLabel);
    }
}
