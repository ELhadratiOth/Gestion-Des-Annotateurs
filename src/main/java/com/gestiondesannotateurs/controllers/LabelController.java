package com.gestiondesannotateurs.controllers;


import com.gestiondesannotateurs.dtos.LabelCreate;
import com.gestiondesannotateurs.entities.Label;
import com.gestiondesannotateurs.interfaces.LabelService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/labels")
public class LabelController {
    @Autowired
    private LabelService labelService;


    @PostMapping
    public ResponseEntity<?> createLabel(@RequestBody @Valid LabelCreate labelCreate){
        Label label =  labelService.createLabel(labelCreate);


        return new ResponseEntity<>( label , HttpStatus.CREATED);
    }
}
