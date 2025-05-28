package com.gestiondesannotateurs.controllers;

import com.gestiondesannotateurs.dtos.LabelResponse;
import com.gestiondesannotateurs.dtos.TeamCreateDto;
import com.gestiondesannotateurs.entities.Annotator;
import com.gestiondesannotateurs.repositories.TaskToDoRepo;
import com.gestiondesannotateurs.services.TeamService;
import com.gestiondesannotateurs.shared.Exceptions.GlobalSuccessHandler;
import com.gestiondesannotateurs.shared.GlobalResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
public class TeamController {

    @Autowired
    private TeamService teamService;

    @GetMapping()
    public ResponseEntity<GlobalResponse<List<?>>> getAnnotatorsGroupedByDataset() {
        return   GlobalSuccessHandler.success(teamService.getAnnotatorsGroupedByDataset());
    }
}
