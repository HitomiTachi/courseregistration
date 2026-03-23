package com.example.courseregistration.repository;

import com.example.courseregistration.entity.Course;
import com.example.courseregistration.entity.Enrollment;
import com.example.courseregistration.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

	List<Enrollment> findByStudent_Id(Long studentId);

	@Query("""
			SELECT e FROM Enrollment e
			JOIN FETCH e.course c
			LEFT JOIN FETCH c.category
			WHERE e.student.id = :studentId
			ORDER BY e.enrollDate DESC
			""")
	List<Enrollment> findByStudentIdWithCourseAndCategory(@Param("studentId") Long studentId);

	boolean existsByStudentAndCourse(Student student, Course course);
}
