package com.gestiondesannotateurs.services;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import com.gestiondesannotateurs.dtos.AnnotatorTaskDto;
import com.gestiondesannotateurs.dtos.AnnotatorWithTaskId;
import com.gestiondesannotateurs.entities.Dataset;
import com.gestiondesannotateurs.entities.TaskToDo;
import com.gestiondesannotateurs.repositories.AnnotationRepo;
import com.gestiondesannotateurs.repositories.DatasetRepo;
import com.gestiondesannotateurs.repositories.TaskToDoRepo;
import com.gestiondesannotateurs.shared.Exceptions.CustomResponseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gestiondesannotateurs.dtos.AnnotatorDto;
import com.gestiondesannotateurs.entities.Annotator;
import com.gestiondesannotateurs.interfaces.AnnotatorService;
import com.gestiondesannotateurs.repositories.AnnotatorRepo;
import com.gestiondesannotateurs.shared.Exceptions.AnnotatorNotFoundException;

@Service
@Transactional
public class AnnotatorServiceImpl implements AnnotatorService {
    @Autowired
    private AnnotatorRepo annotatorRepository;

    @Autowired
    private TaskToDoRepo taskToDoRepo;

    @Autowired
    private DatasetRepo datasetRepo;

    @Autowired
    private EmailService emailService;

    @Autowired
    private AnnotationRepo annotationRepo;


    @Override
    public Annotator getAnnotatorById(Long annotatorId) {
        return annotatorRepository.findById(annotatorId)
                .orElseThrow(() -> new AnnotatorNotFoundException(annotatorId));
    }

    @Override
    public Annotator getLastAnnotatorById() {
        return annotatorRepository.findTopByOrderByIdDesc();
    }

    @Override
    public Annotator getAnnotatorByEmail(String email) {
        Annotator annotator=annotatorRepository.findByEmail(email);
        if(annotator==null){
            new CustomResponseException(404,"No user found with this email");
        }
        return annotator;
    }

    @Override
    public List<Annotator> getAllAnnotators() {
        return annotatorRepository.findAll();
    }

    @Override
    public Annotator createAnnotator( AnnotatorDto annotator) {
    	Annotator newAnnotator = new Annotator();
        String token = UUID.randomUUID().toString();

        newAnnotator.setFirstName(annotator.getFirstName());
		newAnnotator.setLastName(annotator.getLastName());
		newAnnotator.setEmail(annotator.getEmail());
//        newAnnotator.setUserName(annotator.getUsername());
        newAnnotator.setSpammer(false);
        newAnnotator.setActive(true);
        newAnnotator.setVerified(false);
        newAnnotator.setActive(annotator.getActive());
        newAnnotator.setAccountCreationToken(token);
    	if (annotatorRepository.existsByEmail(newAnnotator.getEmail())) {
			throw new AnnotatorNotFoundException(newAnnotator.getId());
		}
        emailService.sendAccountCreationEmail(newAnnotator.getEmail(), token);

        return annotatorRepository.save(newAnnotator);
    }

    @Override
    public Annotator updateAnnotator(Long annotatorId, AnnotatorDto annotator) {
    	Annotator existingAnnotator = annotatorRepository.findById(annotatorId)
				.orElseThrow(() -> new AnnotatorNotFoundException(annotatorId));

    	Annotator currentAnnotator= annotatorRepository.findById(existingAnnotator.getId()).get();

    	String firstName = annotator.getFirstName();
        currentAnnotator.setFirstName(firstName);

    	String lastName = annotator.getLastName();
        currentAnnotator.setLastName(lastName);

//    	String login = annotator.getLogin();
//        currentAnnotator.setUserName(login);

    	String email = annotator.getEmail();
        currentAnnotator.setEmail(email);

    	return annotatorRepository.save(currentAnnotator);
    }

    @Override
    public void deleteAnnotator(Long annotatorId) {
        if (!annotatorRepository.existsById(annotatorId)) {
            throw new AnnotatorNotFoundException(annotatorId);
        }
        annotatorRepository.deleteById(annotatorId);
    }
    
    public void markAsSpammer(Long id) {
        Annotator annotator = annotatorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Annotateur introuvable"));

        annotator.setSpammer(true);
        annotatorRepository.save(annotator);
    }

    public void deactivateAnnotator(Long id) {
		Annotator annotator = annotatorRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Annotateur introuvable"));

        if(annotator.isActive()){
            annotator.setActive(false);
        }
        else{
            annotator.setActive(true);
        }
		annotatorRepository.save(annotator);
	}

    public void activateAnnotator(Long id) {
        Annotator annotator = annotatorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Annotateur introuvable"));

        annotator.setActive(true);
        annotatorRepository.save(annotator);
    }

    public List<Annotator> getAnnotatorSpamers(Long datasetId) {
        Dataset dataset = datasetRepo.findById(datasetId)
                .orElseThrow(() -> new AnnotatorNotFoundException(datasetId));
        List<Annotator> spamers = dataset.getTasks().stream()
                .map(TaskToDo::getAnnotator)
                .filter(Annotator::isSpammer)
                .distinct()
                .collect(Collectors.toList());
        return spamers;
    }
    @Override
    public List<AnnotatorTaskDto> getAnnotatorsByDataset(Long datasetId) {
        Dataset dataset = datasetRepo.findById(datasetId)
                .orElseThrow(() -> new CustomResponseException(404, "Dataset introuvable"));

        return dataset.getTasks().stream()
                .map(task -> new AnnotatorTaskDto(
                        task.getId(),
                        task.getAnnotator().getId(),
                        task.getAnnotator().getFirstName(),
                        task.getAnnotator().getLastName(),
                        task.getAnnotator().getUsername(),
                        task.getAnnotator().getEmail()
                ))
                .collect(Collectors.toList());
    }
    public Annotator getAnnotatorByTask(Long taskId){
        Optional<TaskToDo> task= Optional.ofNullable(taskToDoRepo.findById(taskId)
                .orElseThrow(() -> new CustomResponseException(404, "No such task")));
        Annotator annotator = task.get().getAnnotator();
        return annotator;
    }
    @Override
    public List<Annotator> getMatchingAnnotators (String name){
        List<Annotator> annotators = annotatorRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(name,name);
        return annotators;

    }

    @Override
    public Map<String, Map.Entry<Long, Long>> getAnnotatorsStats() {
        List<Annotator> annotators = annotatorRepository.findAll();
        List<Map.Entry<Long, Long>> statsList = new ArrayList<>();
        for (Annotator annotator : annotators) {
            if (!annotator.isSpammer()) {
                Long annotationCount = annotationRepo.findByAnnotatorId(annotator.getId()).stream().count();
                statsList.add(new AbstractMap.SimpleEntry<>(annotator.getId(), annotationCount));
            }
        }

        if (statsList.isEmpty()) {
            return Map.of();
        }
        // Trier par nombre d’annotations
        statsList.sort(Comparator.comparingLong(Map.Entry::getValue));

        Map<String, Map.Entry<Long, Long>> result = new HashMap<>();
        result.put("min", statsList.get(0));
        result.put("median", statsList.get(statsList.size() / 2));
        result.put("max", statsList.get(statsList.size() - 1));

        return result;
    }



}
