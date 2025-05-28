package com.gestiondesannotateurs.services;

import com.gestiondesannotateurs.dtos.AnnotatorTeamDto;
import com.gestiondesannotateurs.dtos.TeamCreateDto;
import com.gestiondesannotateurs.entities.Annotator;
import com.gestiondesannotateurs.entities.Dataset;
import com.gestiondesannotateurs.entities.TaskToDo;
import com.gestiondesannotateurs.interfaces.DatasetService;
import com.gestiondesannotateurs.repositories.TaskToDoRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TeamService {
    @Autowired
    private DatasetService datasetService;

    public List<TeamCreateDto> getAnnotatorsGroupedByDataset() {
        List<Dataset> datasets = datasetService.getAssignedDatasets();
        System.out.println("dataset size :" +datasets.size());
        if(datasets.isEmpty()){
            return new ArrayList<>();
        }
        System.out.println(datasets.get(0).getName());

        List<TeamCreateDto>  teams = new ArrayList<>() ;
        for (Dataset dataset : datasets) {
            List<AnnotatorTeamDto> annotators = new ArrayList<>();

            for(TaskToDo task : dataset.getTasks()){

                AnnotatorTeamDto annotCustom = new AnnotatorTeamDto(
                        task.getAnnotator().getFirstName(),
                        task.getAnnotator().getLastName(),
                        task.getAnnotator().getUsername(),
                        task.getAnnotator().getEmail()
                );
                annotators.add(annotCustom);


            }
            TeamCreateDto team = new TeamCreateDto(dataset.getName() ,dataset.getAdvancement(),dataset.getCreatedAt(),dataset.getLabel().getName() ,annotators );
            teams.add(team);
        }

        return teams;


    }


}
