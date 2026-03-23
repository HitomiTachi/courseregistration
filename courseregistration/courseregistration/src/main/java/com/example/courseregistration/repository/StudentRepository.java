package com.example.courseregistration.repository;

import com.example.courseregistration.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {

	Optional<Student> findByUsername(String username);

	Optional<Student> findByEmail(String email);

	default Optional<Student> findByUsernameOrEmail(String principalName) {
		return findByUsername(principalName).or(() -> findByEmail(principalName));
	}

	boolean existsByUsername(String username);

	boolean existsByEmail(String email);
}
