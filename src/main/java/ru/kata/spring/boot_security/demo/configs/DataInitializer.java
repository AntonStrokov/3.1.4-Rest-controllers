package ru.kata.spring.boot_security.demo.configs;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.dto.UserDto;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.List;
import java.util.Set;


@Configuration
@RequiredArgsConstructor
public class DataInitializer {

	private final RoleService roleService;
	private final UserService userService;

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
		boolean adminExists = userService.getAllUsersDto()
				.stream()
				.anyMatch(u -> u.getEmail().equalsIgnoreCase(email));

		if (!adminExists) {
			UserDto adminDto = new UserDto();
			adminDto.setName(name);
			adminDto.setEmail(email);
			adminDto.setAge(30);
			adminDto.setPassword(rawPassword);

			List<Long> roleIds = roles.stream()
					.map(Role::getId)
					.toList();
			adminDto.setRoleIds(roleIds);

			userService.addUser(adminDto);
		}
	}
}
