package com.gestiondesannotateurs.services;


import com.gestiondesannotateurs.config.JwtHelper;
import com.gestiondesannotateurs.dtos.LoginRequest;
import com.gestiondesannotateurs.dtos.LoginResp;
import com.gestiondesannotateurs.dtos.PersonDTO;
import com.gestiondesannotateurs.dtos.SignupRequest;
import com.gestiondesannotateurs.entities.Admin;
import com.gestiondesannotateurs.entities.Annotator;
import com.gestiondesannotateurs.entities.Othman;
import com.gestiondesannotateurs.entities.Person;
import com.gestiondesannotateurs.repositories.AnnotationRepo;
import com.gestiondesannotateurs.repositories.PersonRepo;
import com.gestiondesannotateurs.shared.Exceptions.CustomResponseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthServiceImpl {
    @Autowired
    private PersonRepo personRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtHelper jwtHelper;

    public PersonDTO signup(SignupRequest signupRequest) {
        System.out.println("tokennn : " + signupRequest.token());

        Person person = personRepo.findOneByAccountCreationToken(signupRequest.token())
                .orElseThrow(() -> new CustomResponseException(404, "Invalid token"));

        if (person.isVerified()) {
            throw new CustomResponseException(404, "Account already created");
        }

        person.setUserName(signupRequest.username());
        person.setPassword(passwordEncoder.encode(signupRequest.password()));
        person.setVerified(true);
        person.setAccountCreationToken(null);

        Person savedPerson = personRepo.save(person);

        return new PersonDTO(
                savedPerson.getId(),
                savedPerson.getFirstName(),
                savedPerson.getLastName(),
                savedPerson.getEmail(),
                savedPerson.getUsername(),
                savedPerson.getRole()
        );
    }


    public LoginResp login(LoginRequest loginRequest ) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.username(),
                        loginRequest.password()
                )
        );

        Person user = personRepo.findOneByUserName(loginRequest.username())
                .orElseThrow(() ->  new CustomResponseException(404,"Account already created"));



        Map<String, Object> customClaims = new HashMap<>();
        customClaims.put("userId", user.getId());


        PersonDTO personDTO = new PersonDTO(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getUsername(),
                user.getRole()
        );

        return  new LoginResp(jwtHelper.generateToken(customClaims, user) , personDTO) ;
    }
}