package com.gestiondesannotateurs.services;


import com.gestiondesannotateurs.config.JwtHelper;
import com.gestiondesannotateurs.dtos.LoginRequest;
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

    public void signup(SignupRequest signupRequest, String token) {
        Person person = personRepo.findOneByAccountCreationToken(token)
                .orElseThrow(() -> new CustomResponseException(404,"Invalid token"));

        if (person.isVerified()) {
            throw  new CustomResponseException(404,"Account already created");
        }

        person.setUserName(signupRequest.username());
        person.setPassword(passwordEncoder.encode(signupRequest.password()));
        person.setVerified(true);
        person.setAccountCreationToken(null);

        personRepo.save(person);

    }


    public String login(LoginRequest loginRequest) {
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
        return jwtHelper.generateToken(customClaims, user);
    }
}