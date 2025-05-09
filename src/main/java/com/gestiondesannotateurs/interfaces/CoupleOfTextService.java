package com.gestiondesannotateurs.interfaces;

import com.gestiondesannotateurs.dtos.CoupletextDto;
import com.gestiondesannotateurs.entities.Dataset;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface CoupleOfTextService {
    Long createRows(Dataset dataset , MultipartFile file) throws CsvValidationException, IOException;
    List<CoupletextDto> findDtoByDataset(Dataset dataset, Pageable pageable);
    List <CoupletextDto> getCouplesByTaskPaged(Long taskId, Pageable pageable);
}
