package com.example.courseregistration.controller;

import com.example.courseregistration.entity.Course;
import com.example.courseregistration.service.CourseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

	private static final int PAGE_SIZE = 5;
	private final CourseService courseService;

	public HomeController(CourseService courseService) {
		this.courseService = courseService;
	}

	@GetMapping({ "/", "/home" })
	public String home(@RequestParam(defaultValue = "0") int page,
			@RequestParam(name = "q", required = false) String q, Model model) {
		Pageable pageable = PageRequest.of(Math.max(page, 0), PAGE_SIZE, Sort.by(Sort.Direction.ASC, "id"));
		String keyword = q != null ? q.trim() : "";
		Page<Course> coursePage = keyword.isEmpty()
				? courseService.findAll(pageable)
				: courseService.searchByName(keyword, pageable);
		model.addAttribute("coursePage", coursePage);
		model.addAttribute("q", keyword);
		model.addAttribute("searchActive", !keyword.isEmpty());
		return "home";
	}
}
