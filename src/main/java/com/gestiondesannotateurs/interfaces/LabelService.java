package com.gestiondesannotateurs.interfaces;

import com.gestiondesannotateurs.dtos.LabelCreate;
import com.gestiondesannotateurs.entities.Label;

public interface LabelService {
    Label createLabel(LabelCreate label);
}
