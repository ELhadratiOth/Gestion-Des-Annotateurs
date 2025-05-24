package com.gestiondesannotateurs.controllers;


import com.gestiondesannotateurs.services.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stats")
public class StatsController {

    @Autowired
    private StatsService statsService;

    public ResponseEntity<?> getAdminStats(){
        return null;
    }



}
