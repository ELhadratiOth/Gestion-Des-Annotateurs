package com.gestiondesannotateurs.services;


import com.gestiondesannotateurs.dtos.StatsResp;
import com.gestiondesannotateurs.interfaces.AnnotatorService;
import com.gestiondesannotateurs.repositories.AnnotatorRepo;
import com.gestiondesannotateurs.repositories.DatasetRepo;
import com.gestiondesannotateurs.repositories.PersonRepo;
import com.gestiondesannotateurs.repositories.TaskToDoRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StatsService {

    @Autowired
    private PersonRepo personRepo;

    @Autowired
    private DatasetRepo datasetRepo;

    @Autowired
    private TaskToDoRepo taskToDoRepo ;



    public StatsResp getStats(){

        Long nbrTasks = taskToDoRepo.count();
        Long nbrUsers= personRepo.count();
        Long nbrDatasets = datasetRepo.count();
        Long nbrAnnotatorsActive = personRepo.countByRoleAndActive("ANNOTATOR" ,true);
        Long nbrAnnotators = personRepo.countByRole("ANNOTATOR");


        return null;

    }

}
