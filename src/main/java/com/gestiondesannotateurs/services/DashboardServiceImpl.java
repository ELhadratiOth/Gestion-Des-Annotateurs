package com.gestiondesannotateurs.services;

import com.gestiondesannotateurs.dtos.AnnotatorStatsDto;
import com.gestiondesannotateurs.entities.*;
import com.gestiondesannotateurs.repositories.*;
import com.gestiondesannotateurs.interfaces.DashboardService;
import com.gestiondesannotateurs.shared.Exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class DashboardServiceImpl implements DashboardService {

    @Autowired private AnnotatorRepo annotatorRepo;
    @Autowired private TaskToDoRepo taskToDoRepo;
    @Autowired private AnnotationRepo annotationRepo;
    @Autowired private CoupleOfTextRepo coupleOfTextRepo;
    @Autowired private DatasetRepo datasetRepo;

    @Override
    public AnnotatorStatsDto getAnnotatorStats(Long annotatorId) {
        Annotator annotator = annotatorRepo.findById(annotatorId)
                .orElseThrow(() -> new AnnotatorNotFoundException(annotatorId));

        List<TaskToDo> tasks = taskToDoRepo.findByAnnotatorId(annotatorId);
        long tasksCompleted = tasks.stream()
                .filter(task -> task.getStatus() != null && task.getStatus() >= 100)
                .count();

        return new AnnotatorStatsDto(
                annotator.getFirstName(),
                annotator.getLastName(),
                (int)tasksCompleted,
                tasks.size(),
                calculateAccuracyRate(annotatorId),
                calculateDailyStreak(annotatorId),
                calculateTodayGoal(annotatorId),
                checkSpammerStatus(annotatorId),
                getRecentAnnotations(annotatorId),
                getActiveProjects(annotatorId),
                getTeamActivity()
        );
    }

    private double calculateAccuracyRate(Long annotatorId) {
        List<AnnotationClass> userAnnotations = annotationRepo.findByAnnotatorId(annotatorId);
        int correct = 0;
        int totalCompared = 0;

        for (AnnotationClass userAnnotation : userAnnotations) {
            Optional<AnnotationClass> adminAnnotation = annotationRepo
                    .findIfAlreadyAnnotatedByAdmin(userAnnotation.getCoupletext().getId());

            if (adminAnnotation.isPresent()) {
                totalCompared++;
                if (userAnnotation.getChoosenLabel().equals(adminAnnotation.get().getChoosenLabel())) {
                    correct++;
                }
            }
        }
        return totalCompared > 0 ? (correct * 100.0 / totalCompared) : 0.0;
    }

    private int calculateDailyStreak(Long annotatorId) {
        List<LocalDate> activeDates = annotationRepo.findDistinctAnnotationDatesByAnnotator(annotatorId);
        if (activeDates.isEmpty()) return 0;

        LocalDate currentDate = LocalDate.now();
        int streak = 0;

        if (activeDates.contains(currentDate)) streak++;
        else return 0;

        for (int i = 1; i <= activeDates.size(); i++) {
            if (activeDates.contains(currentDate.minusDays(i))) {
                streak++;
            } else {
                break;
            }
        }
        return streak;
    }

    private int calculateTodayGoal(Long annotatorId) {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        long todayAnnotations = annotationRepo.countAnnotationsByAnnotatorSince(annotatorId, startOfDay);
        return 5 - (int)todayAnnotations;
    }

    private boolean checkSpammerStatus(Long annotatorId) {
        List<AnnotationClass> annotations = annotationRepo.findByAnnotatorId(annotatorId);
        if (annotations.isEmpty()) return false;

        long duplicateAnnotations = annotations.stream()
                .filter(a -> a.getCoupletext().getIsDuplicated())
                .count();

        double duplicateRatio = (double)duplicateAnnotations / annotations.size();
        double accuracy = calculateAccuracyRate(annotatorId);
        return duplicateRatio > 0.5 && accuracy < 60;
    }

    private List<String> getRecentAnnotations(Long annotatorId) {
        return annotationRepo.findTop5RecentAnnotationsByAnnotator(annotatorId)
                .stream()
                .map(a -> String.format("%s - %s",
                        a.getCoupletext().getId(),
                        a.getChoosenLabel()))
                .collect(Collectors.toList());
    }

    private List<String> getActiveProjects(Long annotatorId) {
        return taskToDoRepo.findByAnnotatorId(annotatorId)
                .stream()
                .map(t -> t.getDataset().getName())
                .distinct()
                .collect(Collectors.toList());
    }

    private List<String> getTeamActivity() {
        return annotationRepo.findTop5RecentTeamAnnotations()
                .stream()
                .map(a -> String.format("%s %s - %s (%s)",
                        a.getAnnotator().getFirstName(),
                        a.getAnnotator().getLastName(),
                        a.getCoupletext().getId(),
                        a.getChoosenLabel()))
                .collect(Collectors.toList());
    }
}