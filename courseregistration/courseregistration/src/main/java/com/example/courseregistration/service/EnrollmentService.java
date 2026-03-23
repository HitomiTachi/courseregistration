package com.example.courseregistration.service;

import com.example.courseregistration.entity.Course;
import com.example.courseregistration.entity.Enrollment;
import com.example.courseregistration.entity.Student;
import com.example.courseregistration.repository.CourseRepository;
import com.example.courseregistration.repository.EnrollmentRepository;
import com.example.courseregistration.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EnrollmentService {

	private final EnrollmentRepository enrollmentRepository;
	private final StudentRepository studentRepository;
	private final CourseRepository courseRepository;

	public EnrollmentService(EnrollmentRepository enrollmentRepository, StudentRepository studentRepository,
			CourseRepository courseRepository) {
		this.enrollmentRepository = enrollmentRepository;
		this.studentRepository = studentRepository;
		this.courseRepository = courseRepository;
	}

	@Transactional
	public Enrollment enroll(Long studentId, Long courseId) {
		Student student = studentRepository.findById(studentId)
				.orElseThrow(() -> new IllegalArgumentException("Student not found."));
		Course course = courseRepository.findById(courseId)
				.orElseThrow(() -> new IllegalArgumentException("Course not found."));
		if (enrollmentRepository.existsByStudentAndCourse(student, course)) {
			throw new IllegalStateException("Already enrolled in this course.");
		}
		Enrollment enrollment = Enrollment.builder()
				.student(student)
				.course(course)
				.enrollDate(LocalDateTime.now())
				.build();
		return enrollmentRepository.save(enrollment);
	}

	@Transactional(readOnly = true)
	public List<Enrollment> getMyEnrollments(Long studentId) {
		return enrollmentRepository.findByStudentIdWithCourseAndCategory(studentId);
	}
}
