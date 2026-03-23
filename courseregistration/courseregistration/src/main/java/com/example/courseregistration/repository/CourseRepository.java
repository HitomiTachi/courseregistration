package com.example.courseregistration.repository;

import com.example.courseregistration.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {

	@EntityGraph(attributePaths = { "category" })
	Optional<Course> findById(Long id);

	@EntityGraph(attributePaths = { "category" })
	Page<Course> findAll(Pageable pageable);

	@EntityGraph(attributePaths = { "category" })
	Page<Course> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
