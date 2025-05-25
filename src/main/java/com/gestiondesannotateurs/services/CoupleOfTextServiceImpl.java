package com.gestiondesannotateurs.services;

import com.gestiondesannotateurs.dtos.CoupletextDto;
import com.gestiondesannotateurs.entities.*;
import com.gestiondesannotateurs.interfaces.CoupleOfTextService;
import com.gestiondesannotateurs.interfaces.KappaEvaluationService;
import com.gestiondesannotateurs.repositories.AnnotationRepo;
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
import java.util.*;
import java.util.stream.Collectors;



@Service
public class CoupleOfTextServiceImpl implements CoupleOfTextService {

    @Autowired
    private CoupleOfTextRepo coupleOfTextRepo;

    @Autowired
    private ProcessFile processFile ;

    @Autowired
    private AnnotationRepo annotationRepo;

    @Autowired
    private KappaEvaluationService kappaEvaluationService;

    @Override
    public Long createRows(Dataset dataset , MultipartFile file) throws CsvValidationException, IOException {
//        System.out.println("bugg hh");


        if(file == null || file.isEmpty() ){
            throw  new CustomResponseException(401 , "File is null or empty");
        }

//        System.out.println("bugg hhh");

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

    public List<CoupletextDto> getCouplesByTaskPaged(Long taskId, Pageable pageable) {
        Page<Coupletext> page = coupleOfTextRepo.findByTasks_Id(taskId,pageable);
        if (page.isEmpty()) {
            throw new CustomResponseException(404, "Aucun couple de texte trouvé");
        }
        return page.getContent()
                .stream()
                .map(c -> new CoupletextDto(c.getId(), c.getTextA(), c.getTextB()))
                .collect(Collectors.toList());
    }

    public void computeTrueLabelsForDataset(Dataset dataset){

        List<Coupletext> couples = coupleOfTextRepo.findByDataset(dataset);

        for (Coupletext couple : couples) {
            List<AnnotationClass> allAnnotations = annotationRepo.findByCoupletextId(couple.getId());

            // Filtre  : no spammeur, no admin
            List<AnnotationClass> validAnnotations = allAnnotations.stream()
                    .filter(a -> !a.getIsAdmin()) // exclude admin annotations
                    .filter(a -> {
                        Person annotateur = a.getAnnotator();
                        if (annotateur instanceof Annotator) {
                            return !((Annotator) annotateur).isSpammer(); //exclude spamers annotations
                        } else {
                            return false;
                        }
                    })
                    .collect(Collectors.toList());

            // get all annotator assigned to the couple
            Set<Long> assignedAnnotatorIds = couple.getTasks().stream()
                    .map(task -> task.getAnnotator().getId())
                    .collect(Collectors.toSet());

            // Get annotator who really annotated the couple
            Set<Long> annotatorsWhoAnnotated = validAnnotations.stream()
                    .map(a -> a.getAnnotator().getId())
                    .collect(Collectors.toSet());

            if (!annotatorsWhoAnnotated.containsAll(assignedAnnotatorIds)) {
                couple.setTrueLabel("Not Yet");

            }

            // Mapper les labels en entiers
            Map<Integer, String> categoryLabels = new HashMap<>();
            List<Integer> numericLabels = new ArrayList<>();
            int index = 0;

            for (AnnotationClass annotation : validAnnotations) {
                String label = annotation.getChoosenLabel();
                if (!categoryLabels.containsValue(label)) {
                    categoryLabels.put(index++, label);
                }
                int numericLabel = getKeyByValue(categoryLabels, label);
                numericLabels.add(numericLabel);
            }

            // Appel à ta méthode de calcul du label dominant (par ex. via kappa)
            String mostFrequentLabel = kappaEvaluationService.getMostFrequentCategoryWithKappa(
                    numericLabels,
                    categoryLabels
            );

            couple.setTrueLabel(mostFrequentLabel);


        }


    }

    private <K, V> K getKeyByValue(Map<K, V> map, V value) {
        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (entry.getValue().equals(value)) {
                return entry.getKey();
            }
        }
        return null;
    }



}
