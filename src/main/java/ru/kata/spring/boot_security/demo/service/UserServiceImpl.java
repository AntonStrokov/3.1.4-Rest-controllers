package ru.kata.spring.boot_security.demo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.dao.RoleDao;
import ru.kata.spring.boot_security.demo.dao.UserDao;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;

import javax.persistence.EntityNotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserDao userDao;
	private final PasswordEncoder passwordEncoder;
	private final RoleDao roleDao;

	@Override
	@Transactional
	public void addUser(User user) {
		log.info("Adding new user: email={}", user.getEmail());
		user.setPassword(passwordEncoder.encode(user.getPassword()));

		if (user.getRoles() == null || user.getRoles().isEmpty()) {
			Role userRole = roleDao.getRoleByName("ROLE_USER").orElseThrow();
			user.setRoles(Collections.singleton(userRole));
			log.info("No roles provided, assigned default ROLE_USER");
		}
		else {
			user.setRoles(fetchRolesFromDb(user.getRoles()));
			log.info("Assigned roles: {}", user.getRoles().stream().map(Role::getName).collect(Collectors.toList()));
		}

		userDao.addUser(user);
		log.info("User successfully added: email={}", user.getEmail());
	}

	@Override
	@Transactional
	public void updateUser(User user) {
		log.info("Updating user: id={}, email={}", user.getId(), user.getEmail());

		User managedUser = userDao.getUserById(user.getId())
				.orElseThrow(() -> new EntityNotFoundException("User not found"));

		log.debug("User found in DB: id={}, oldName={}, oldEmail={}, oldRoles={}",
				managedUser.getId(), managedUser.getName(), managedUser.getEmail(),
				managedUser.getRoles().stream().map(Role::getName).collect(Collectors.toList()));

		managedUser.setName(user.getName());
		managedUser.setEmail(user.getEmail());
		managedUser.setAge(user.getAge());

		if (user.getPassword() != null && !user.getPassword().trim().isEmpty()) {
			managedUser.setPassword(passwordEncoder.encode(user.getPassword()));
			log.debug("Password was updated");
		} else {
			log.debug("Password was not changed (empty)");
		}

		if (user.getRoles() != null && !user.getRoles().isEmpty()) {
			Set<Role> managedRoles = fetchRolesFromDb(user.getRoles());
			managedUser.setRoles(managedRoles);
			log.info("User roles updated: id={}, newRoles={}",
					user.getId(),
					managedRoles.stream().map(Role::getName).collect(Collectors.toList()));
		}

		log.info("User update completed: id={}, email={}", managedUser.getId(), managedUser.getEmail());
	}

	private Set<Role> fetchRolesFromDb(Set<Role> rolesFromForm) {
		return rolesFromForm.stream()
				.map(role -> {
					log.debug("Fetching role from DB: roleId={}", role.getId());
					return roleDao.getRoleById(role.getId())
							.orElseThrow(() -> new EntityNotFoundException("Role not found with id: " + role.getId()));
				})
				.collect(Collectors.toSet());
	}

	@Override
	@Transactional
	public void removeUser(Long id) {
		log.info("Removing user: id={}", id);
		userDao.removeUser(id);
		log.info("User removed: id={}", id);
	}

	@Override
	@Transactional(readOnly = true)
	public User getUserById(Long id) {
		User user = userDao.getUserById(id)
				.orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
		log.debug("User retrieved: id={}, email={}, roles={}",
				user.getId(), user.getEmail(),
				user.getRoles().stream().map(Role::getName).collect(Collectors.toList()));
		return user;
	}

	@Override
	@Transactional(readOnly = true)
	public List<User> getAllUsers() {
		List<User> users = userDao.getAllUsers();
		log.debug("Retrieved {} users from DB", users.size());
		return users;
	}
}