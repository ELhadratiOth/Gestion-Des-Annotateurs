package com.gestiondesannotateurs.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PersonnDto {
	@NotBlank(message = "First name is required")
	private String firstName;
	@NotBlank(message = "Last name is required")
	private String lastName;
	@NotBlank(message = "Login is required")
	private String username;
	@NotBlank(message = "Email is required")
	@Email(message = "Email should be valid")
	private String email;

//	@NotBlank(message = "Password is required")
//    @Size( min = 8 , max = 10, message = "Password should be between characters long")
//    private String password;

//	not used yet
	private String phone;
	private String jobTitle;
	private String department;
	private String location;
	private String  bio;



}
