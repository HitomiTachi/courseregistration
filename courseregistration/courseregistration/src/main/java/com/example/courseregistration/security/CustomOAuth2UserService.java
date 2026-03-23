package com.example.courseregistration.security;

import com.example.courseregistration.entity.Role;
import com.example.courseregistration.entity.Student;
import com.example.courseregistration.repository.RoleRepository;
import com.example.courseregistration.repository.StudentRepository;
import com.example.courseregistration.service.AuthService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

	private final StudentRepository studentRepository;
	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;

	public CustomOAuth2UserService(StudentRepository studentRepository, RoleRepository roleRepository,
			PasswordEncoder passwordEncoder) {
		this.studentRepository = studentRepository;
		this.roleRepository = roleRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) {
		OAuth2User oauth2User = super.loadUser(userRequest);
		Map<String, Object> attributes = oauth2User.getAttributes();
		String rawEmail = (String) attributes.get("email");
		if (rawEmail == null || rawEmail.isBlank()) {
			throw new OAuth2AuthenticationException(
					new OAuth2Error("email_missing", "Google account has no email.", null));
		}
		final String email = rawEmail.trim();

		Student student = studentRepository.findByEmail(email)
				.or(() -> studentRepository.findByUsername(email))
				.orElseGet(() -> createStudentFromGoogle(email));

		List<GrantedAuthority> authorities = student.getRoles().stream()
				.<GrantedAuthority>map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
				.toList();

		return new DefaultOAuth2User(authorities, attributes, "email");
	}

	private Student createStudentFromGoogle(String email) {
		Role studentRole = roleRepository.findByName(AuthService.ROLE_STUDENT)
				.orElseThrow(() -> new OAuth2AuthenticationException(
						new OAuth2Error("setup_error", "Role STUDENT is not initialized.", null)));
		Set<Role> roles = new HashSet<>();
		roles.add(studentRole);
		Student student = Student.builder()
				.username(email)
				.email(email)
				.password(passwordEncoder.encode(UUID.randomUUID().toString()))
				.roles(roles)
				.build();
		return studentRepository.save(student);
	}
}
