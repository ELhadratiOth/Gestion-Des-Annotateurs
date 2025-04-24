package com.gestiondesannotateurs.intefaces;

import com.gestiondesannotateurs.dtos.DatasetCreate;
import com.gestiondesannotateurs.dtos.LabelCreate;
import com.gestiondesannotateurs.entities.Dataset;
import com.gestiondesannotateurs.entities.Label;

public interface LabelService {
    Label createLabel(LabelCreate label);
}
