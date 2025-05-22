package com.gestiondesannotateurs.services;


import com.gestiondesannotateurs.dtos.PersonnDto;
import com.gestiondesannotateurs.entities.Person;
import com.gestiondesannotateurs.repositories.PersonRepo;
import com.gestiondesannotateurs.shared.Exceptions.CustomResponseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PersonService {
    @Autowired
    private PersonRepo personRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;


    public Person getPerson(Long personId) {
        Optional<Person> person = personRepo.findById(personId);
        if (person.isEmpty()) {
            throw new CustomResponseException(404, "Person not found");
        }
        return person.get();
    }


    public Person updatePerson(Long personId , PersonnDto personnDto) {

        Optional<Person> person = personRepo.findById(personId);
        if (person.isEmpty()) {
            throw new CustomResponseException(404, "Person not found");
        }
        Person p = person.get();
        p.setFirstName(personnDto.getFirstName());
        p.setLastName(personnDto.getLastName());
        p.setEmail(personnDto.getEmail());
        p.setUserName(personnDto.getUsername());
//        p.setPassword(passwordEncoder.encode(personnDto.getPassword()));
        return personRepo.save(p);
    }


}
