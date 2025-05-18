package com.gestiondesannotateurs.controllers;


import com.gestiondesannotateurs.dtos.LoginRequest;
import com.gestiondesannotateurs.dtos.LoginResp;
import com.gestiondesannotateurs.dtos.PersonDTO;
import com.gestiondesannotateurs.dtos.SignupRequest;
import com.gestiondesannotateurs.entities.Person;
import com.gestiondesannotateurs.services.AuthServiceImpl;
import com.gestiondesannotateurs.shared.Exceptions.GlobalSuccessHandler;
import com.gestiondesannotateurs.shared.GlobalResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthServiceImpl authService;


    @PostMapping("/login")
    public ResponseEntity<GlobalResponse<LoginResp>> login(
            @RequestBody LoginRequest loginRequest
    ) {
        System.out.println(loginRequest);

        return GlobalSuccessHandler.success("login successful" , authService.login(loginRequest) );
    }

    @PostMapping("/signup")
    public ResponseEntity<GlobalResponse<PersonDTO>> signup(
            @RequestBody SignupRequest signupRequest
    ) {

        PersonDTO person = authService.signup(signupRequest);
        return GlobalSuccessHandler.success( "Signed Up",person);  //Signed Up

    }

}
