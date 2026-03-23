package com.example.courseregistration.config;

import com.example.courseregistration.entity.Role;
import com.example.courseregistration.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class RoleInitializer implements CommandLineRunner {

	private final RoleRepository roleRepository;

	public RoleInitializer(RoleRepository roleRepository) {
		this.roleRepository = roleRepository;
	}

	@Override
	public void run(String... args) {
		ensureRole("STUDENT");
		ensureRole("ADMIN");
	}

	private void ensureRole(String name) {
		if (roleRepository.findByName(name).isEmpty()) {
			Role role = new Role();
			role.setName(name);
			roleRepository.save(role);
		}
	}
}
