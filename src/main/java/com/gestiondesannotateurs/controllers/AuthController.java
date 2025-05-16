package com.gestiondesannotateurs.controllers;


import com.gestiondesannotateurs.dtos.LoginRequest;
import com.gestiondesannotateurs.dtos.SignupRequest;
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
    public ResponseEntity<GlobalResponse<String>> login(
            @RequestBody LoginRequest loginRequest
    ) {
        String token = authService.login(loginRequest);

        return GlobalSuccessHandler.success("login successful" , token);
    }

    @PostMapping("/singup")
    public ResponseEntity<GlobalResponse<Void>> signup(
            @RequestBody SignupRequest signupRequest,
            @RequestParam String token
    ) {

        authService.signup(signupRequest, token);
        return GlobalSuccessHandler.noContent();  //Signed Up

    }

}
