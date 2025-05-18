package com.gestiondesannotateurs.services;


import com.gestiondesannotateurs.config.JwtHelper;
import com.gestiondesannotateurs.dtos.*;
import com.gestiondesannotateurs.entities.*;
import com.gestiondesannotateurs.repositories.AnnotationRepo;
import com.gestiondesannotateurs.repositories.PasswordRestRepo;
import com.gestiondesannotateurs.repositories.PersonRepo;
import com.gestiondesannotateurs.shared.Exceptions.CustomResponseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
    @Autowired
    private EmailService emailService;
    @Autowired
    private PasswordRestRepo passwordRestRepo;

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

    @Transactional
    public void initiatePasswordRest(String username , String email) {
        try {
            Person person = personRepo.findOneByUserName(username)
                    .orElseThrow(() -> new CustomResponseException(404,"Account not found"));

            if (!person.getEmail().equals(email)) {
                throw new CustomResponseException(404,"Account not found");
            }

            String token = UUID.randomUUID().toString();
            LocalDateTime expiry = LocalDateTime.now().plusMinutes(15);

            PasswordResetToken resetToken = new PasswordResetToken();
            resetToken.setToken(token);
            resetToken.setExpiryDate(expiry);
            resetToken.setPerson(person);

            passwordRestRepo.save(resetToken);

            emailService.sendPasswordRestEmail(person.getEmail(), token);
        } catch (Exception e) {
            throw new CustomResponseException(404 , "Sending reset password email failed");
        }
    }


    public void resetPassword(ResetPasswordRequest resetPasswordRequest) {
        PasswordResetToken resetToken = passwordRestRepo.findOneByToken(resetPasswordRequest.token())
                .orElseThrow(() -> new CustomResponseException(404,"Invalid token"));

        boolean isTokenExpired = resetToken.getExpiryDate().isBefore(LocalDateTime.now());
        if (isTokenExpired) {
            passwordRestRepo.delete(resetToken);
            throw new CustomResponseException(404 ,"Token has expired, request a new one");
        }

        Person person = resetToken.getPerson();
        person.setPassword(passwordEncoder.encode(resetPasswordRequest.newPassword()));
        personRepo.save(person);
        passwordRestRepo.delete(resetToken);
    }

}