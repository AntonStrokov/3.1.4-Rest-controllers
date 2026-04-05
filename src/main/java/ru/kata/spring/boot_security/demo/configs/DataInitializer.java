package ru.kata.spring.boot_security.demo.configs;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.Set;


@Configuration
@RequiredArgsConstructor
public class DataInitializer {

	private final RoleService roleService;
	private final UserService userService;
	private final PasswordEncoder passwordEncoder;

	@Bean
	@Transactional
	CommandLineRunner init() {
		return args -> {
			Role adminRole = createRoleIfNotFound("ROLE_ADMIN");
			Role userRole = createRoleIfNotFound("ROLE_USER");

			createAdminIfNotFound(
					"Admin",
					"admin@example.com",
					"admin",
					Set.of(adminRole, userRole)
			);
		};
	}


	private Role createRoleIfNotFound(String name) {
		return roleService.getRoleByName(name)
				.orElseGet(() -> {
					Role role = new Role();
					role.setName(name);
					return roleService.save(role);
				});
	}


	private void createAdminIfNotFound(String name,
	                                   String email,
	                                   String rawPassword,
	                                   Set<Role> roles) {
		boolean adminExists = userService.getAllUsers()
				.stream()
				.anyMatch(u -> u.getEmail().equalsIgnoreCase(email));

		if (!adminExists) {
			User admin = new User();
			admin.setName(name);
			admin.setEmail(email);
			admin.setAge(30);
			admin.setPassword(passwordEncoder.encode(rawPassword));
			admin.setRoles(roles);

			userService.addUser(admin,
					roles.stream()
							.map(Role::getId)
							.toList());
		}
	}
}
