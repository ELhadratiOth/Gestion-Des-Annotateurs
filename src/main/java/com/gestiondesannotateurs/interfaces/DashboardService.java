package com.gestiondesannotateurs.interfaces;

import com.gestiondesannotateurs.dtos.AnnotatorStatsDto;
public interface DashboardService {
    AnnotatorStatsDto getAnnotatorStats(Long annotatorId);
}
