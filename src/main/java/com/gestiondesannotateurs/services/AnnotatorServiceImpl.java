package com.gestiondesannotateurs.services;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import com.gestiondesannotateurs.dtos.*;
import com.gestiondesannotateurs.entities.*;
import com.gestiondesannotateurs.interfaces.TaskService;
import com.gestiondesannotateurs.repositories.*;
import com.gestiondesannotateurs.shared.Exceptions.CustomResponseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gestiondesannotateurs.interfaces.AnnotatorService;
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

    @Autowired
    private CoupleOfTextRepo coupleOfTextRepo;

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

    @Override
    public List<AnnotatorTask> getListOfTasksForAnnotator(Long annotatorId) {
        Annotator annotator = annotatorRepository.findById(annotatorId)
                .orElseThrow(() -> new CustomResponseException(404, "Annotator not found"));

        List<AnnotatorTask> tasks = new ArrayList<>();

        List <TaskToDo> tasksAssociated =taskToDoRepo.findByAnnotatorId(annotator.getId());

        for (TaskToDo taskToDo : tasksAssociated) {

            // Récupérer les coupletexts affectés à cet annotateur pour ce dataset
            List<Coupletext> coupletextsAssigned = taskToDo.getCoupletexts();
            Long totalAssigned = (long) coupletextsAssigned.size();

            if (totalAssigned == 0) continue; // Aucun exemple affecté

            // Calcul de l'avancement
            double progress = taskToDo.getStatus();
            String status = progress >= 100.0 ? "Completed" :
                    progress == 0.0 ? "Not Start" : "In Progress";
            String action = status.equals("Completed") ? "Review" :
                    status.equals("Not Start") ? "Start" : "Continue";

            AnnotatorTask task = new AnnotatorTask(
                    taskToDo.getId(),
                    taskToDo.getDataset().getName(),
                    taskToDo.getDataset().getDescription(),
                    taskToDo.getDataset().getLabel().getName(),
                    progress,
                    status,
                    action,
                    totalAssigned
            );

            tasks.add(task);
        }

        return tasks;
    }

    @Override
    public List<CoupleOfTextWithAnnotation> getCoupletextsWithAnnotationByAnnotator(Long annotatorId, Long taskId) {
        Annotator annotator = annotatorRepository.findById(annotatorId)
                .orElseThrow(() -> new CustomResponseException(404, "Annotator not found"));

        TaskToDo task = taskToDoRepo.findById(taskId)
                .orElseThrow(() -> new CustomResponseException(404, "Task not found"));

        if (!task.getAnnotator().getId().equals(annotatorId)) {
            throw new CustomResponseException(403, "This task does not belong to the annotator");
        }

        Dataset dataset = task.getDataset();
        List<Coupletext> allCouples = task.getCoupletexts();

        // Catégorisation
        List<Coupletext> sharedWithAdmin = new ArrayList<>();
        List<Coupletext> duplicated = new ArrayList<>();
        List<Coupletext> normals = new ArrayList<>();

        for (Coupletext ct : allCouples) {
            if (Boolean.TRUE.equals(ct.getIsAnnotatedByAdmin())) {
                sharedWithAdmin.add(ct);
            } else if (Boolean.TRUE.equals(ct.getIsDuplicated())) {
                duplicated.add(ct);
            } else {
                normals.add(ct);
            }
        }

        // Sélection des 5 duplicated et 10 normal
        List<Coupletext> first5Duplicated = duplicated.stream().limit(5).toList();
        List<Coupletext> first10Normals = normals.stream().limit(10).toList();
        List<Coupletext> remainingNormals = normals.stream().skip(10).toList();

        // Construction de la liste dans l'ordre voulu
        List<Coupletext> orderedList = new ArrayList<>();
        orderedList.addAll(sharedWithAdmin);                  // Étape 1
        orderedList.addAll(first5Duplicated);                 // Étape 2
        orderedList.addAll(first10Normals);                   // Étape 3
        orderedList.addAll(first5Duplicated);                 // Étape 4 (réinsertion)
        orderedList.addAll(remainingNormals);                 // Étape 5

        // Construction des objets à retourner
        List<CoupleOfTextWithAnnotation> result = new ArrayList<>();

        for (Coupletext ct : orderedList) {
            List <AnnotationClass> annotations = annotationRepo.findByAnnotatorIdAndCoupletextId(annotator.getId(), ct.getId());
            AnnotationClass annotation = null;
            if (!annotations.isEmpty()) {
                // on prend la dernière annotation (la plus récente)
                annotation = annotations.stream()
                        .sorted(Comparator.comparing(AnnotationClass::getCreatedAt).reversed())
                        .findFirst()
                        .orElse(null);
            }

            result.add(new CoupleOfTextWithAnnotation(
                    ct.getId(),
                    annotation != null ? annotation.getId() : null,
                    ct.getTextA(),
                    ct.getTextB(),
                    annotation != null ? annotation.getChoosenLabel() : null,
                    dataset.getName(),
                    dataset.getLabel().getName(),
                    dataset.getLabel().getClasses()
            ));

        }

        return result;
    }



}
