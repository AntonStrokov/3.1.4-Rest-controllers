package ru.kata.spring.boot_security.demo.configs;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder; // Внедряем!
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.dao.RoleDao;
import ru.kata.spring.boot_security.demo.dao.UserDao;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.Set;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

	private final UserDao userDao;
	private final RoleDao roleDao;
	private final PasswordEncoder passwordEncoder;

	@Bean
	@Transactional
	CommandLineRunner init() {
		return args -> {
			Role adminRole = createRoleIfNotFound("ROLE_ADMIN");
			Role userRole = createRoleIfNotFound("ROLE_USER");

			createAdminIfNotFound("Admin", "admin@example.com", "admin", Set.of(adminRole, userRole));
		};
	}

	private Role createRoleIfNotFound(String name) {
		return roleDao.getRoleByName(name).orElseGet(() -> {
			Role role = new Role();
			role.setName(name);
			roleDao.addRole(role);
			return role;
		});
	}

	private void createAdminIfNotFound(String name, String email, String password, Set<Role> roles) {
		if (userDao.getAllUsers().stream().noneMatch(u -> u.getEmail().equals(email))) {
			User admin = new User();
			admin.setName(name);
			admin.setEmail(email);
			admin.setAge(30);
			admin.setPassword(passwordEncoder.encode(password));
			admin.setRoles(roles);
			userDao.addUser(admin);
		}
	}
}
