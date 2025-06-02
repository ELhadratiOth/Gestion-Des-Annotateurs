package com.gestiondesannotateurs.services;

import com.gestiondesannotateurs.dtos.AnnotatorStatsDto;
import com.gestiondesannotateurs.entities.*;
import com.gestiondesannotateurs.interfaces.DashboardService;
import com.gestiondesannotateurs.repositories.*;
import com.gestiondesannotateurs.shared.Exceptions.*;
import com.gestiondesannotateurs.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class DashboardServiceImpl implements DashboardService {

    @Autowired private AnnotatorRepo annotatorRepo;
    @Autowired private TaskToDoRepo taskToDoRepo;
    @Autowired private AnnotationRepo annotationRepo;
    @Autowired private DatasetRepo datasetRepo;


    @Override
    public AnnotatorStatsDto getAnnotatorStats(Long annotatorId) {
        Annotator annotator = annotatorRepo.findById(annotatorId)
                .orElseThrow(() -> new AnnotatorNotFoundException(annotatorId));

        List<TaskToDo> tasks = taskToDoRepo.findByAnnotatorId(annotatorId);
        long tasksCompleted = tasks.stream()
                .filter(task -> task.getStatus() != null && task.getStatus() >= 100.0)
                .count();

        return new AnnotatorStatsDto(
                annotator.getFirstName(),
                annotator.getLastName(),
                (int)tasksCompleted,
                tasks.size(),
                calculateDaysSinceCreation(annotator),
                calculateDailyStreak(annotatorId),
                calculateTodayGoal(annotatorId),
                checkSpammerStatus(annotatorId),
                getRecentAnnotations(annotatorId),
                getActiveProjects(annotatorId),
                getTeamActivity(annotatorId)
        );
    }

    private long calculateDaysSinceCreation(Annotator annotator) {
        if (annotator.getCreationDate() == null) return 0;
        return ChronoUnit.DAYS.between(
                annotator.getCreationDate(),  // Pas besoin de toLocalDate() car c'est déjà un LocalDateTime
                LocalDateTime.now()          // Utiliser LocalDateTime.now() au lieu de LocalDate.now()
        );
    }

    private int calculateDailyStreak(Long annotatorId) {
        List<LocalDate> activeDates = annotationRepo.findDistinctAnnotationDatesByAnnotator(annotatorId)
                .stream()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());

        if (activeDates.isEmpty()) return 0;

        LocalDate currentDate = LocalDate.now();
        if (!activeDates.get(0).equals(currentDate)) return 0;

        int streak = 1;
        LocalDate previousDate = currentDate.minusDays(1);

        for (int i = 1; i < activeDates.size(); i++) {
            if (activeDates.get(i).equals(previousDate)) {
                streak++;
                previousDate = previousDate.minusDays(1);
            } else {
                break;
            }
        }
        return streak;
    }

    private String calculateTodayGoal(Long annotatorId) {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        long todayAnnotations = annotationRepo.countAnnotationsByAnnotatorSince(annotatorId, startOfDay);

        List<TaskToDo> incompleteTasks = taskToDoRepo.findByAnnotatorIdAndStatusLessThan(annotatorId, 100.0);
        long totalCouplesToAnnotate = incompleteTasks.stream()
                .flatMap(task -> task.getCoupletexts().stream())
                .distinct()
                .count();

        return todayAnnotations + "/" + totalCouplesToAnnotate;
    }

    private boolean checkSpammerStatus(Long annotatorId) {
        List<AnnotationClass> annotations = annotationRepo.findByAnnotatorId(annotatorId);
        if (annotations.isEmpty()) return false;

        long duplicateAnnotations = annotations.stream()
                .filter(a -> a.getCoupletext().getIsDuplicated())
                .count();

        double duplicateRatio = (double)duplicateAnnotations / annotations.size();
        return duplicateRatio > 0.5;
    }

    private List<String> getRecentAnnotations(Long annotatorId) {
        return annotationRepo.findTop5RecentAnnotationsByAnnotator(annotatorId)
                .stream()
                .map(a -> StringUtils.safeSubstring(a.getCoupletext().getTextA() ,10) +"/"+StringUtils.safeSubstring(a.getCoupletext().getTextB(),10)  + " - " + a.getChoosenLabel())
                .collect(Collectors.toList());
    }

    private List<String> getActiveProjects(Long annotatorId) {
        return taskToDoRepo.findByAnnotatorId(annotatorId)
                .stream()
                .map(t -> t.getDataset().getName())
                .distinct()
                .collect(Collectors.toList());
    }

    private List<String> getTeamActivity(Long annotatorId) {
        return datasetRepo.findSharedDatasetsWithAnnotatorCount(annotatorId)
                .stream()
                .map(ds -> String.format("%s (%d annotateurs)",
                        ds.getDatasetName(),  // Utilisez getDatasetName() au lieu de getDataset().getName()
                        ds.getAnnotatorCount()))
                .collect(Collectors.toList());
    }
}