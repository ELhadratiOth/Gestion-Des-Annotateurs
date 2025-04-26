package com.gestiondesannotateurs.controllers;


import com.gestiondesannotateurs.dtos.LabelCreate;
import com.gestiondesannotateurs.dtos.LabelResponse;
import com.gestiondesannotateurs.entities.Label;
import com.gestiondesannotateurs.interfaces.LabelService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/labels")
public class LabelController {
    @Autowired
    private LabelService labelService;


    @GetMapping
    public ResponseEntity<List<LabelResponse>> getAllLabels() {
        return new ResponseEntity<>(labelService.getAll() , HttpStatus.OK);
    }


    @PostMapping
    public ResponseEntity<?> createLabel(@RequestBody @Valid LabelCreate labelCreate){
        Label label =  labelService.createLabel(labelCreate);
        LabelResponse labelResponse = new LabelResponse(label.getId() , label.getName());



        return new ResponseEntity<>( labelResponse , HttpStatus.CREATED);
    }


}
