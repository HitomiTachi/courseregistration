package com.example.courseregistration.controller;

import com.example.courseregistration.dto.RegisterForm;
import com.example.courseregistration.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
@Controller
public class AuthController {

	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@ModelAttribute("q")
	public String defaultCourseSearchQuery() {
		return "";
	}

	@GetMapping("/login")
	public String login() {
		return "auth/login";
	}

	@GetMapping("/register")
	public String registerForm(Model model) {
		model.addAttribute("registerForm", new RegisterForm());
		return "auth/register";
	}

	@PostMapping("/register")
	public String register(@Valid @ModelAttribute("registerForm") RegisterForm form, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return "auth/register";
		}
		try {
			authService.register(form.getUsername(), form.getPassword(), form.getEmail());
		}
		catch (IllegalArgumentException ex) {
			bindingResult.reject("registerError", ex.getMessage());
			return "auth/register";
		}
		catch (IllegalStateException ex) {
			bindingResult.reject("registerError", ex.getMessage());
			return "auth/register";
		}
		return "redirect:/login?registered";
	}
}
