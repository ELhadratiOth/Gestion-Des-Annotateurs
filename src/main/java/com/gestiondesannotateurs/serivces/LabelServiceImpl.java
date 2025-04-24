package com.gestiondesannotateurs.serivces;

import com.gestiondesannotateurs.dtos.DatasetCreate;
import com.gestiondesannotateurs.dtos.LabelCreate;
import com.gestiondesannotateurs.entities.Dataset;
import com.gestiondesannotateurs.entities.Label;
import com.gestiondesannotateurs.intefaces.DatasetService;
import com.gestiondesannotateurs.intefaces.LabelService;
import com.gestiondesannotateurs.repositories.DatasetRepo;
import com.gestiondesannotateurs.repositories.LabelRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
