package com.gestiondesannotateurs.controllers;


import com.gestiondesannotateurs.dtos.*;
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

    @PostMapping("/forgot-password/")
    public ResponseEntity<GlobalResponse<String>> forgotPassword(@RequestBody ResetPasswordReq resetPasswordReq) {

        authService.initiatePasswordRest(resetPasswordReq.username(),resetPasswordReq.email() );
        return GlobalSuccessHandler.success(  "Password reset email sent!",null);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<GlobalResponse<String>> resetPassword(
            @RequestBody ResetPasswordRequest resetPasswordRequest
    ) {
        authService.resetPassword(resetPasswordRequest);
        return GlobalSuccessHandler.success(  "Password reset successfully!",null);
    }

}
