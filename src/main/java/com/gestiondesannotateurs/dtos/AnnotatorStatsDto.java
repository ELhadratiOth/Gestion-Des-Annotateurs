package com.gestiondesannotateurs.dtos;

import lombok.*;

import java.util.List;
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AnnotatorStatsDto {
    private String firstName;
    private String lastName;
    private int tasksCompleted;
    private int totalTasks;
    private long daysSinceCreation;
    private int dailyStreak;
    private String todayGoal;
    private boolean isSpammer;
    private List<String> recentAnnotations;
    private List<String> activeProjects;
    private List<String> teamActivity;
}