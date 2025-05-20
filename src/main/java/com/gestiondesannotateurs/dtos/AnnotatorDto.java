package com.gestiondesannotateurs.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnnotatorDto {
	@NotBlank(message = "First name is required")
	private String firstName;
	@NotBlank(message = "Last name is required")
	private String lastName;
//	@NotBlank(message = "Login is required")
//	private String username;
	@NotBlank(message = "Email is required")
	@Email(message = "Email should be valid")
	private String email;
	
}
