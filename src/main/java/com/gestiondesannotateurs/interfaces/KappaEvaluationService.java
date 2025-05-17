package com.gestiondesannotateurs.interfaces;

import java.util.List;

public interface KappaEvaluationService {
    double calculateKappa(List<List<Integer>> annotations);
}