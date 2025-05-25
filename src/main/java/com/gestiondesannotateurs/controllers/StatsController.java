package com.gestiondesannotateurs.controllers;


import com.gestiondesannotateurs.dtos.StatsResp;
import com.gestiondesannotateurs.services.StatsService;
import com.gestiondesannotateurs.shared.Exceptions.GlobalSuccessHandler;
import com.gestiondesannotateurs.shared.GlobalResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stats")
public class StatsController {

    @Autowired
    private StatsService statsService;


    @GetMapping()
    public ResponseEntity<?> getAdminStats(){
        StatsResp stats = statsService.getStats();
        return GlobalSuccessHandler.success("Stats of the applicatioins",stats);

    }
}
