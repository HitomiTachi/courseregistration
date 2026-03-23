package com.example.courseregistration.service;

import com.example.courseregistration.entity.Role;
import com.example.courseregistration.entity.Student;
import com.example.courseregistration.repository.RoleRepository;
import com.example.courseregistration.repository.StudentRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class AuthService {

	public static final String ROLE_STUDENT = "STUDENT";

	private final StudentRepository studentRepository;
	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;

	public AuthService(StudentRepository studentRepository, RoleRepository roleRepository,
			PasswordEncoder passwordEncoder) {
		this.studentRepository = studentRepository;
		this.roleRepository = roleRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Transactional
	public Student register(String username, String rawPassword, String email) {
		String u = username != null ? username.trim() : "";
		String e = email != null ? email.trim() : "";
		if (u.isEmpty() || e.isEmpty() || rawPassword == null || rawPassword.isEmpty()) {
			throw new IllegalArgumentException("Username, password and email are required.");
		}
		if (studentRepository.existsByUsername(u)) {
			throw new IllegalArgumentException("Username already exists.");
		}
		if (studentRepository.existsByEmail(e)) {
			throw new IllegalArgumentException("Email already exists.");
		}

		Role studentRole = roleRepository.findByName(ROLE_STUDENT)
				.orElseThrow(() -> new IllegalStateException("Role STUDENT is not initialized in database."));

		Set<Role> roles = new HashSet<>();
		roles.add(studentRole);
		Student student = Student.builder()
				.username(u)
				.password(passwordEncoder.encode(rawPassword))
				.email(e)
				.roles(roles)
				.build();

		return studentRepository.save(student);
	}

	/**
	 * Optional credential check (form login thường do Spring Security xử lý sau PHASE 5).
	 */
	public Optional<Student> login(String username, String rawPassword) {
		if (username == null || rawPassword == null) {
			return Optional.empty();
		}
		return studentRepository.findByUsername(username.trim()).filter(s -> passwordEncoder.matches(rawPassword, s.getPassword()));
	}
}
