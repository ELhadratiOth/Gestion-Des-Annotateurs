package com.gestiondesannotateurs.services;

import com.gestiondesannotateurs.dtos.CoupletextDto;
import com.gestiondesannotateurs.dtos.PagedCoupletextDto;
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
    private ProcessFile processFile;

    @Autowired
    private AnnotationRepo annotationRepo;

    @Autowired
    private KappaEvaluationService kappaEvaluationService;

    @Override
    public Long createRows(Dataset dataset, MultipartFile file) throws CsvValidationException, IOException {
        if(file == null || file.isEmpty()){
            throw new CustomResponseException(401, "File is null or empty");
        }

        List<Coupletext> storageDatas = processFile.processFile(file, dataset);
        coupleOfTextRepo.saveAll(storageDatas);

        return Long.valueOf(storageDatas.size());
    }

    @Override
    public PagedCoupletextDto findDtoByDataset(Dataset dataset, Pageable pageable) {
        // Calcul des vrais labels
        computeTrueLabelsForDataset(dataset);

        Page<Coupletext> page = coupleOfTextRepo.findByDataset(dataset, pageable);
        if (page.isEmpty()) {
            throw new CustomResponseException(404, "No couple found");
        }

        List<CoupletextDto> coupleDtos = page.getContent()
                .stream()
                .map(c -> new CoupletextDto(
                        c.getId(),
                        c.getTextA(),
                        c.getTextB(),
                        c.getTrueLabel().toUpperCase()
                ))
                .collect(Collectors.toList());

        return new PagedCoupletextDto(
                page.getTotalElements(),
                page.getTotalPages(),
                coupleDtos,
                page.getNumber()

        );
    }


    @Override
    public List<CoupletextDto> getCouplesByTaskPaged(Long taskId, Pageable pageable) {
        Page<Coupletext> page = coupleOfTextRepo.findByTasks_Id(taskId, pageable);
        if (page.isEmpty()) {
            throw new CustomResponseException(404, "Aucun couple de texte trouvé");
        }

        // Compute true labels for all couples in this task
        page.getContent().forEach(c -> {
            if (c.getDataset() != null) {
                computeTrueLabelsForDataset(c.getDataset());
            }
        });

        return page.getContent()
                .stream()
                .map(c -> new CoupletextDto(
                        c.getId(),
                        c.getTextA(),
                        c.getTextB(),
                        c.getTrueLabel() != null ? c.getTrueLabel().toUpperCase() : "NOT_YET"
                ))
                .collect(Collectors.toList());
    }

    @Override
    public void computeTrueLabelsForDataset(Dataset dataset) {
        List<Coupletext> couples = coupleOfTextRepo.findByDataset(dataset);

        for (Coupletext couple : couples) {
            List<AnnotationClass> allAnnotations = annotationRepo.findByCoupletextId(couple.getId());

            // Filter valid annotations (no spammer, no admin)
            List<AnnotationClass> validAnnotations = allAnnotations.stream()
                    .filter(a -> !a.getIsAdmin())
                    .filter(a -> {
                        Person annotateur = a.getAnnotator();
                        if (annotateur instanceof Annotator) {
                            return !((Annotator) annotateur).isSpammer();
                        }
                        return false;
                    })
                    .collect(Collectors.toList());

            // Get assigned annotators
            Set<Long> assignedAnnotatorIds = couple.getTasks().stream()
                    .map(task -> task.getAnnotator().getId())
                    .collect(Collectors.toSet());

            // Get annotators who actually annotated
            Set<Long> annotatorsWhoAnnotated = validAnnotations.stream()
                    .map(a -> a.getAnnotator().getId())
                    .collect(Collectors.toSet());

            if (!annotatorsWhoAnnotated.containsAll(assignedAnnotatorIds)) {
                couple.setTrueLabel("NOT_YET");
                coupleOfTextRepo.save(couple);
                continue;
            }

            // Map labels to integers for kappa calculation
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

            // Calculate most frequent label with kappa
            String mostFrequentLabel = kappaEvaluationService.getMostFrequentCategoryWithKappa(
                    numericLabels,
                    categoryLabels
            );

            couple.setTrueLabel(mostFrequentLabel != null ? mostFrequentLabel.toUpperCase() : "UNDEFINED");
            coupleOfTextRepo.save(couple);
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