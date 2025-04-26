package com.gestiondesannotateurs.interfaces;

import com.gestiondesannotateurs.entities.Dataset;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface CoupleOfTextService {
    Long createRows(Dataset dataset , MultipartFile file) throws CsvValidationException, IOException;
}
