package com.example.courseregistration.security;

import com.example.courseregistration.entity.Student;
import com.example.courseregistration.repository.StudentRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	private final StudentRepository studentRepository;

	public CustomUserDetailsService(StudentRepository studentRepository) {
		this.studentRepository = studentRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Student student = studentRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

		List<GrantedAuthority> authorities = student.getRoles().stream()
				.<GrantedAuthority>map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
				.toList();

		return User.builder()
				.username(student.getUsername())
				.password(student.getPassword())
				.authorities(authorities)
				.build();
	}
}
