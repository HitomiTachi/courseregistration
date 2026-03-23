package com.example.courseregistration.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterForm {

	@NotBlank(message = "Username is required")
	@Size(min = 3, max = 100, message = "Username must be between 3 and 100 characters")
	private String username;

	@NotBlank(message = "Password is required")
	@Size(min = 6, max = 100, message = "Password must be at least 6 characters")
	private String password;

	@NotBlank(message = "Email is required")
	@Email(message = "Invalid email format")
	@Size(max = 255)
	private String email;
}
