package com.gestiondesannotateurs.interfaces;

import com.gestiondesannotateurs.dtos.blackListDto;

import java.util.List;
import java.util.Map;

public interface SpamService {
    Map<Long, Double> detectSpammer(Long datasetId);

    blackListDto getSpammerById(Long annotatorId);

    List<blackListDto> getAllSpammers();
}