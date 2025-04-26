package com.gestiondesannotateurs.services;

import com.gestiondesannotateurs.entities.Coupletext;
import com.gestiondesannotateurs.entities.Dataset;
import com.gestiondesannotateurs.interfaces.CoupleOfTextService;
import com.gestiondesannotateurs.repositories.CoupleOfTextRepo;
import com.gestiondesannotateurs.utils.ProcessFile;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class CoupleOfTextServiceImpl implements CoupleOfTextService {

    @Autowired
    private CoupleOfTextRepo coupleOfTextRepo;

    @Autowired
    private ProcessFile processFile ;

    @Override
    public Long createRows(Dataset dataset , MultipartFile file) throws CsvValidationException, IOException {


        List<Coupletext> storageDatas =  processFile.processFile(file );

        coupleOfTextRepo.saveAll(storageDatas);

        return Long.valueOf((long) storageDatas.size()) ;

    }


}
