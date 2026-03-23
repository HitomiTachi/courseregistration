package com.example.courseregistration.service;

import com.example.courseregistration.entity.Category;
import com.example.courseregistration.entity.Course;
import com.example.courseregistration.repository.CategoryRepository;
import com.example.courseregistration.repository.CourseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CourseService {

	private final CourseRepository courseRepository;
	private final CategoryRepository categoryRepository;

	public CourseService(CourseRepository courseRepository, CategoryRepository categoryRepository) {
		this.courseRepository = courseRepository;
		this.categoryRepository = categoryRepository;
	}

	@Transactional(readOnly = true)
	public Page<Course> findAll(Pageable pageable) {
		return courseRepository.findAll(pageable);
	}

	@Transactional(readOnly = true)
	public Page<Course> searchByName(String name, Pageable pageable) {
		if (name == null || name.isBlank()) {
			return courseRepository.findAll(pageable);
		}
		return courseRepository.findByNameContainingIgnoreCase(name.trim(), pageable);
	}

	@Transactional(readOnly = true)
	public Optional<Course> findById(Long id) {
		return courseRepository.findById(id);
	}

	@Transactional
	public Course create(String name, String image, Integer credits, String lecturer, Long categoryId) {
		validateCourseFields(name, credits, lecturer);
		Course course = new Course();
		course.setName(name.trim());
		course.setImage(image != null ? image.trim() : null);
		course.setCredits(credits);
		course.setLecturer(lecturer.trim());
		course.setCategory(resolveCategory(categoryId));
		return courseRepository.save(course);
	}

	@Transactional
	public Course update(Long id, String name, String image, Integer credits, String lecturer, Long categoryId) {
		Course course = courseRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Course not found."));
		validateCourseFields(name, credits, lecturer);
		course.setName(name.trim());
		course.setImage(image != null ? image.trim() : null);
		course.setCredits(credits);
		course.setLecturer(lecturer.trim());
		course.setCategory(resolveCategory(categoryId));
		return courseRepository.save(course);
	}

	@Transactional
	public void deleteById(Long id) {
		if (!courseRepository.existsById(id)) {
			throw new IllegalArgumentException("Course not found.");
		}
		courseRepository.deleteById(id);
	}

	private void validateCourseFields(String name, Integer credits, String lecturer) {
		if (name == null || name.isBlank()) {
			throw new IllegalArgumentException("Course name is required.");
		}
		if (credits == null || credits < 0) {
			throw new IllegalArgumentException("Credits must be zero or positive.");
		}
		if (lecturer == null || lecturer.isBlank()) {
			throw new IllegalArgumentException("Lecturer is required.");
		}
	}

	private Category resolveCategory(Long categoryId) {
		if (categoryId == null) {
			return null;
		}
		return categoryRepository.findById(categoryId)
				.orElseThrow(() -> new IllegalArgumentException("Category not found."));
	}
}
