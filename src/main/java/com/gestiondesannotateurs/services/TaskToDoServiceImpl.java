package com.gestiondesannotateurs.services;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.gestiondesannotateurs.dtos.TaskCreate;
import com.gestiondesannotateurs.dtos.TaskToDoDto;
import com.gestiondesannotateurs.entities.*;
import com.gestiondesannotateurs.interfaces.AnnotationService;
import com.gestiondesannotateurs.interfaces.TaskService;
import com.gestiondesannotateurs.repositories.*;
import com.gestiondesannotateurs.shared.Exceptions.CustomResponseException;
import com.gestiondesannotateurs.utils.AdminAnnotationSplitter;
import com.gestiondesannotateurs.utils.CoupleOfTextSplitResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TaskToDoServiceImpl implements TaskService {

    @Autowired
    private DatasetRepo datasetRepo ;

    @Autowired
    private AnnotatorRepo annotatorRepo;

    @Autowired
    private TaskToDoRepo taskToDoRepo ;

    @Autowired
    private CoupleOfTextRepo coupleOfTextRepo;

    @Autowired
    private AnnotationRepo annotationRepo;
    @Autowired
    AnnotationService annotationService;

    @Autowired
    AdminAnnotationSplitter adminAnnotationSplitter;


    @Override
    @Transactional
    public void createTask(TaskCreate tasks) {
        // Validate number of annotators (minimum 3)
        Integer numberOfAnnotators = tasks.annotatorIds().size();
        if (numberOfAnnotators < 3) {
            throw new CustomResponseException(400, "You must have at least 3 annotators");
        }

        // Validate dataset
        Dataset dataset = datasetRepo.findById(tasks.datasetId())
                .orElseThrow(() -> new CustomResponseException(404, "Dataset not found"));

        dataset.setIsAssigned(true);
        dataset.setAffectationDate(LocalDateTime.now());
        datasetRepo.save(dataset);

        // Validate annotators and ensure they are active
        List<Annotator> annotators = new ArrayList<>();
        for (Long annotatorId : tasks.annotatorIds()) {
            Annotator annotator = annotatorRepo.findById(annotatorId)
                    .orElseThrow(() -> new CustomResponseException(404, "No such annotator with ID: " + annotatorId));
            if (!annotator.isActive()) {
                throw new CustomResponseException(400, "Annotator with ID: " + annotatorId + " is deactivated");
            }
            annotators.add(annotator);
        }

        // Create one TaskToDo per annotator (4 tasks for 4 annotators)
        List<TaskToDo> taskList = new ArrayList<>();
        for (Annotator annotator : annotators) {
            TaskToDo task = new TaskToDo();
            task.setAnnotator(annotator);
            task.setDataset(dataset);
            task = taskToDoRepo.save(task);
            taskList.add(task);
        }

        // Fetch Coupletext entities for the dataset (2 couple texts)
        List<Coupletext> coupletexts = coupleOfTextRepo.findByDataset(dataset);

        if (coupletexts.isEmpty()) {
            throw new CustomResponseException(400, "No couple texts found for dataset");
        }

        CoupleOfTextSplitResult coupleTextsAdminSplitter = AdminAnnotationSplitter.splitCoupletexts(coupletexts);
        List<Coupletext> adminList = coupleTextsAdminSplitter.getAdminList();
        coupletexts = coupleTextsAdminSplitter.getOthersList();

        for (Coupletext coupletext : adminList) {
            coupletext.setIsAnnotatedByAdmin(true);
            coupletext.setTasks(taskList);
            coupleOfTextRepo.save(coupletext);
        }

        // Track task assignments for fairness
        Map<TaskToDo, Integer> taskAssignmentCount = new HashMap<>();
        for (TaskToDo task : taskList) {
            taskAssignmentCount.put(task, 0);
        }

        // Assign 3 tasks to each Coupletext (2 × 3 = 6 rows in association table)
        for (Coupletext coupletext : coupletexts) {
            // Get tasks already assigned to this Coupletext
            List<TaskToDo> assignedTasks = coupletext.getTasks();

            // Filter available tasks (not assigned to this Coupletext)
            List<TaskToDo> availableTasks = taskList.stream()
                    .filter(t -> !assignedTasks.contains(t))
                    .collect(Collectors.toList());

            // Ensure enough tasks are available
            if (availableTasks.size() < 3) {
                throw new CustomResponseException(400, "Not enough available tasks for Coupletext ID: " + coupletext.getId());
            }

            // Shuffle for randomization
            Collections.shuffle(availableTasks);

            // Sort by assignment count for fairness (least assigned first)
            availableTasks.sort(Comparator.comparingInt(taskAssignmentCount::get));

            // Select 3 tasks
            List<TaskToDo> selectedTasks = availableTasks.subList(0, 3);

            // Assign tasks to Coupletext and update counts
            for (TaskToDo task : selectedTasks) {
                coupletext.addTask(task);
                taskAssignmentCount.put(task, taskAssignmentCount.get(task) + 1);
            }
            coupleOfTextRepo.save(coupletext);
        }

        // Duplicate some couple of text of each annotator for spamers detection purpose
        int repetitionsPerAnnotator = 5;

        for (TaskToDo task : taskList) {
            // get assigned couples of text

            List<Coupletext> assignedCouples = coupleOfTextRepo.findByTasks_Id(task.getId());

            // Filtrer les couples qui ne sont pas annotés par l'admin
            List<Coupletext> filteredCouples = assignedCouples.stream()
                    .filter(ct -> !ct.getIsAnnotatedByAdmin())
                    .collect(Collectors.toList());


            if (filteredCouples.size() < repetitionsPerAnnotator) {
                throw new CustomResponseException(400, "Not enough couples to duplicate for annotator ID: " + task.getAnnotator().getId());
            }

            // randomly select the couple to duplicates
            Collections.shuffle(filteredCouples);
            List<Coupletext> toDuplicate = filteredCouples.subList(0, repetitionsPerAnnotator);

            for (Coupletext ct : toDuplicate) {
                // Si ce couple est déjà marqué comme duplicata, on le laisse comme tel
                ct.setIsDuplicated(true);

                // Sinon on l'assigne à cette tâche
                if (!ct.getTasks().contains(task)) {
                    ct.addTask(task);
                }

                coupleOfTextRepo.save(ct);
            }
            // Mark the datasets as assigned
            if (!dataset.getIsAssigned()) {
                dataset.setIsAssigned(true);
                dataset.setAffectationDate(LocalDateTime.now());
                datasetRepo.save(dataset);
            }




        }

    }

    public List<Coupletext> getDuplicatedCoupletextsForTask(Long taskId) {
        TaskToDo task = taskToDoRepo.findById(taskId)
                .orElseThrow(() -> new CustomResponseException(404, "Task not found with id: " + taskId));

        return task.getCoupletexts().stream()
                .filter(ct -> Boolean.TRUE.equals(ct.getIsDuplicated()))
                .collect(Collectors.toList());
    }



    @Override
    public List<TaskToDo> getAll() {
        List<TaskToDo> tasks = taskToDoRepo.findAll();
        return tasks;
    }




    public List<TaskToDo> getTasksByAnnotatorId(Long annotatorId) {
        return taskToDoRepo.findByAnnotatorId(annotatorId);
    }

    @Override
    public List<TaskToDoDto> getTasksByDatasetId(Long datasetId) {
        List<TaskToDo> tasks = taskToDoRepo.findByDatasetId(datasetId);
        return tasks.stream()
                .map(task -> new TaskToDoDto(task.getId()
                        , task.getAnnotator().getId() ,
                        task.getAnnotator().getFirstName() + " " + task.getAnnotator().getLastName()
                        ))
                .collect(Collectors.toList());    }


    public Coupletext getNextUnannotatedCoupletextForTask(Long taskId) {
        TaskToDo task = taskToDoRepo.findById(taskId)
                .orElseThrow(() -> new CustomResponseException(404, "No such task"));

        Annotator annotator = task.getAnnotator();

        List<AnnotationClass> annotations = annotationRepo.findByAnnotatorId(annotator.getId());

        Set<Long> annotatedIds = annotations.stream()
                .map(a -> a.getCoupletext().getId())
                .collect(Collectors.toSet());

        // Tous les couples uniques
        List<Coupletext> allCouples = task.getCoupletexts().stream().distinct().toList();

        // Séparer les couples en catégories
        List<Coupletext> sharedWithAdmin = new ArrayList<>();
        List<Coupletext> duplicates = new ArrayList<>();
        List<Coupletext> normals = new ArrayList<>();

        for (Coupletext ct : allCouples) {
            if (Boolean.TRUE.equals(ct.getIsAnnotatedByAdmin())) {
                sharedWithAdmin.add(ct);
            } else if (Boolean.TRUE.equals(ct.getIsDuplicated())) {
                duplicates.add(ct);
            } else {
                normals.add(ct);
            }
        }

        // Compter combien l’annotateur a déjà annoté dans chaque groupe
        long annotatedDupes = annotations.stream()
                .map(a -> a.getCoupletext())
                .filter(ct -> Boolean.TRUE.equals(ct.getIsDuplicated()))
                .count();

        long annotatedNormals = annotations.stream()
                .map(a -> a.getCoupletext())
                .filter(ct -> !Boolean.TRUE.equals(ct.getIsDuplicated())
                        && !Boolean.TRUE.equals(ct.getIsAnnotatedByAdmin()))
                .count();

        // Étape 1 : admin non annotés
        for (Coupletext ct : sharedWithAdmin) {
            if (!annotatedIds.contains(ct.getId())) return ct;
        }

        // Étape 2 : duplicatas (1)
        if (annotatedDupes < 5) {
            for (Coupletext ct : duplicates) {
                if (!annotatedIds.contains(ct.getId())) return ct;
            }
        }

        // Étape 3 : normaux (10)
        if (annotatedDupes >= 5 && annotatedNormals < 10) {
            for (Coupletext ct : normals) {
                if (!annotatedIds.contains(ct.getId())) return ct;
            }
        }

        // Étape 4 : duplicatas restants
        if (annotatedDupes >= 5 && annotatedNormals >= 10) {
            for (Coupletext ct : duplicates) {
                if (!annotatedIds.contains(ct.getId())) return ct;
            }
        }

        // Étape 5 : normaux restants
        if (annotatedDupes >= 5 && annotatedNormals >= 10) {
            for (Coupletext ct : normals) {
                if (!annotatedIds.contains(ct.getId())) return ct;
            }
        }

        // Rien trouvé
        return null;
    }



    public double getProgressForTask(Long taskId, Long annotatorId) {
        long total = coupleOfTextRepo.countByTasks_Id(taskId);
        long done = annotationService.countAnnotationsForAnnotator(annotatorId);
        return (double) done / total * 100.0;
    }

    public void deleteTaskByDatasetId(Long datasetId) {
        Optional<Dataset> dataset = datasetRepo.findById(datasetId);
        if(dataset.isEmpty()){
            throw new CustomResponseException(404,"Dataset doesnt exist with this id");
        }
        taskToDoRepo.deleteAllByDatasetId(datasetId);
        List<Coupletext> coupletexts = coupleOfTextRepo.findByDataset(dataset.get());
        for (Coupletext coupletext : coupletexts) {
            coupletext.setTasks(null);
            coupleOfTextRepo.save(coupletext);
        }


    }


}
