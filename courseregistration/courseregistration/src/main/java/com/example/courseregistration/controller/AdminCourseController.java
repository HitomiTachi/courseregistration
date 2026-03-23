package com.example.courseregistration.controller;

import com.example.courseregistration.dto.CourseForm;
import com.example.courseregistration.entity.Category;
import com.example.courseregistration.entity.Course;
import com.example.courseregistration.repository.CategoryRepository;
import com.example.courseregistration.service.CourseService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/courses")
public class AdminCourseController {

	private static final int PAGE_SIZE = 10;

	private final CourseService courseService;
	private final CategoryRepository categoryRepository;

	public AdminCourseController(CourseService courseService, CategoryRepository categoryRepository) {
		this.courseService = courseService;
		this.categoryRepository = categoryRepository;
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Long.class, "categoryId", new CustomNumberEditor(Long.class, true));
	}

	@ModelAttribute("categories")
	public List<Category> categories() {
		return categoryRepository.findAll(Sort.by("name"));
	}

	@ModelAttribute("q")
	public String navbarCourseSearchQuery() {
		return "";
	}

	@GetMapping
	public String list(@RequestParam(defaultValue = "0") int page, Model model) {
		Pageable pageable = PageRequest.of(Math.max(page, 0), PAGE_SIZE, Sort.by(Sort.Direction.ASC, "id"));
		model.addAttribute("coursePage", courseService.findAll(pageable));
		return "admin/courses/list";
	}

	@GetMapping("/new")
	public String newForm(Model model) {
		model.addAttribute("courseForm", new CourseForm());
		return "admin/courses/form";
	}

	@PostMapping
	public String create(@Valid @ModelAttribute("courseForm") CourseForm form, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return "admin/courses/form";
		}
		courseService.create(form.getName(), form.getImage(), form.getCredits(), form.getLecturer(), form.getCategoryId());
		return "redirect:/admin/courses";
	}

	@GetMapping("/{id}/edit")
	public String editForm(@PathVariable Long id, Model model) {
		Course course = courseService.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		CourseForm form = new CourseForm();
		form.setId(course.getId());
		form.setName(course.getName());
		form.setImage(course.getImage());
		form.setCredits(course.getCredits());
		form.setLecturer(course.getLecturer());
		if (course.getCategory() != null) {
			form.setCategoryId(course.getCategory().getId());
		}
		model.addAttribute("courseForm", form);
		return "admin/courses/form";
	}

	@PostMapping("/{id}")
	public String update(@PathVariable Long id, @Valid @ModelAttribute("courseForm") CourseForm form,
			BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return "admin/courses/form";
		}
		courseService.update(id, form.getName(), form.getImage(), form.getCredits(), form.getLecturer(), form.getCategoryId());
		return "redirect:/admin/courses";
	}

	@PostMapping("/{id}/delete")
	public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
		courseService.deleteById(id);
		redirectAttributes.addFlashAttribute("successMessage", "Course deleted.");
		return "redirect:/admin/courses";
	}
}
