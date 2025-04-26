package com.gestiondesannotateurs.interfaces;

import com.gestiondesannotateurs.dtos.LabelCreate;
import com.gestiondesannotateurs.dtos.LabelResponse;
import com.gestiondesannotateurs.entities.Label;

import java.util.List;

public interface LabelService {
    Label createLabel(LabelCreate label);
    List<LabelResponse> getAll();
}
