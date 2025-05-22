package com.gestiondesannotateurs.controllers;
import com.gestiondesannotateurs.dtos.AdminDto;
import com.gestiondesannotateurs.dtos.AdminDtoo;
import com.gestiondesannotateurs.dtos.CoupleOfTextWithAnnotation;
import com.gestiondesannotateurs.dtos.PersonnDto;
import com.gestiondesannotateurs.entities.Admin;
import com.gestiondesannotateurs.entities.Person;
import com.gestiondesannotateurs.interfaces.AdminService;
import com.gestiondesannotateurs.repositories.PersonRepo;
import com.gestiondesannotateurs.services.PersonService;
import com.gestiondesannotateurs.shared.Exceptions.GlobalSuccessHandler;
import com.gestiondesannotateurs.shared.GlobalResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/persons")
public class PersonController {
    @Autowired
    private PersonService personService;




    @GetMapping("/{personId}")
    @PreAuthorize("hasAnyRole('SUPER-ADMIN','ADMIN','ANNOTATOR')")
    public ResponseEntity<GlobalResponse<Person>> getPersonDetails(@PathVariable Long personId) {
        return GlobalSuccessHandler.success(personService.getPerson(personId));

    }



    @PutMapping("/{personId}")
    @PreAuthorize("hasAnyRole('SUPER-ADMIN','ADMIN','ANNOTATOR')")
    public ResponseEntity<GlobalResponse<Person>> updatePersonDetails(
            @PathVariable Long personId,
            @Valid @RequestBody PersonnDto personnDto) {
        Person updatedAdmin = personService.updatePerson(personId, personnDto);
        return GlobalSuccessHandler.success("Person updated successfully", updatedAdmin);
    }




}
