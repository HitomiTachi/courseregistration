package com.example.courseregistration.controller;

import com.example.courseregistration.entity.Enrollment;
import com.example.courseregistration.entity.Student;
import com.example.courseregistration.repository.StudentRepository;
import com.example.courseregistration.service.EnrollmentService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

/**
 * Principal is resolved from {@link org.springframework.security.core.context.SecurityContext}
 * via injected {@link Authentication} (same as {@code SecurityContextHolder.getContext().getAuthentication()}).
 */
@Controller
@RequestMapping("/enroll")
public class EnrollmentController {

	private final EnrollmentService enrollmentService;
	private final StudentRepository studentRepository;

	public EnrollmentController(EnrollmentService enrollmentService, StudentRepository studentRepository) {
		this.enrollmentService = enrollmentService;
		this.studentRepository = studentRepository;
	}

	@GetMapping("/my")
	public String myCourses(Authentication authentication, Model model) {
		String principal = authentication.getName();
		Student student = studentRepository.findByUsernameOrEmail(principal)
				.orElseThrow(() -> new IllegalStateException("Current user is not linked to a student account."));
		List<Enrollment> enrollments = enrollmentService.getMyEnrollments(student.getId());
		model.addAttribute("enrollments", enrollments);
		model.addAttribute("q", "");
		return "enroll/my-courses";
	}

	@PostMapping("/{courseId}")
	public String enroll(@PathVariable Long courseId, @RequestParam(defaultValue = "0") int page,
			@RequestParam(name = "q", required = false) String q, Authentication authentication,
			RedirectAttributes redirectAttributes) {
		String principal = authentication.getName();
		Student student = studentRepository.findByUsernameOrEmail(principal)
				.orElseThrow(() -> new IllegalStateException("Current user is not linked to a student account."));
		try {
			enrollmentService.enroll(student.getId(), courseId);
			redirectAttributes.addFlashAttribute("successMessage", "You have enrolled successfully.");
		}
		catch (IllegalStateException ex) {
			redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
		}
		catch (IllegalArgumentException ex) {
			redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
		}
		UriComponentsBuilder redirect = UriComponentsBuilder.fromPath("/courses")
				.queryParam("page", Math.max(page, 0));
		if (q != null && !q.isBlank()) {
			redirect.queryParam("q", q.trim());
		}
		return "redirect:" + redirect.build(true).toUriString();
	}
}
