package com.gestiondesannotateurs.services;

import com.gestiondesannotateurs.dtos.CoupletextDto;
import com.gestiondesannotateurs.entities.Coupletext;
import com.gestiondesannotateurs.entities.Dataset;
import com.gestiondesannotateurs.interfaces.CoupleOfTextService;
import com.gestiondesannotateurs.repositories.CoupleOfTextRepo;
import com.gestiondesannotateurs.shared.Exceptions.CustomResponseException;
import com.gestiondesannotateurs.utils.ProcessFile;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CoupleOfTextServiceImpl implements CoupleOfTextService {

    @Autowired
    private CoupleOfTextRepo coupleOfTextRepo;

    @Autowired
    private ProcessFile processFile ;

    @Override
    public Long createRows(Dataset dataset , MultipartFile file) throws CsvValidationException, IOException {


        if(file == null || file.isEmpty() ){
            throw  new CustomResponseException(401 , "File is null or empty");
        }


        List<Coupletext> storageDatas =  processFile.processFile(file , dataset);

        coupleOfTextRepo.saveAll(storageDatas);

        return Long.valueOf((long) storageDatas.size()) ;

    }
    @Override
    public List<CoupletextDto> findDtoByDataset(Dataset dataset, Pageable pageable) {
        Page<Coupletext> page = coupleOfTextRepo.findByDataset(dataset, pageable);
        if (page.isEmpty()) {
            throw new CustomResponseException(404, "Aucun couple de texte trouvé");
        }
        return page.getContent()
                .stream()
                .map(c -> new CoupletextDto(c.getId(), c.getTextA(), c.getTextB()))
                .collect(Collectors.toList());
    }


}
