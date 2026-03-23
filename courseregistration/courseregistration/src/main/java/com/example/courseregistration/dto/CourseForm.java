package com.example.courseregistration.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseForm {

	private Long id;

	@NotBlank(message = "Name is required")
	@Size(max = 255)
	private String name;

	@Size(max = 500)
	private String image;

	@NotNull(message = "Credits is required")
	@Min(value = 0, message = "Credits must be zero or positive")
	private Integer credits;

	@NotBlank(message = "Lecturer is required")
	@Size(max = 255)
	private String lecturer;

	private Long categoryId;
}
