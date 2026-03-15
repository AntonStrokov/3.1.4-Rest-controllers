package ru.kata.spring.boot_security.demo.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.dao.UserDao;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import javax.persistence.EntityNotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserDao userDao;
	private final PasswordEncoder passwordEncoder;
	private final RoleService roleService;

	@Override
	@Transactional
	public void addUser(User user, List<Long> roleIds) {
		user.setPassword(passwordEncoder.encode(user.getPassword()));

		if (roleIds != null && !roleIds.isEmpty()) {
			List<Role> roles = roleService.getRolesByIds(roleIds);
			user.setRoles(new HashSet<>(roles));
		} else {
			roleService.getRoleByName("ROLE_USER")
					.ifPresent(r -> user.setRoles(Set.of(r)));
		}

		userDao.addUser(user);
	}

	@Override
	@Transactional
	public void updateUser(User user, List<Long> roleIds) {
		User managedUser = userDao.getUserById(user.getId())
				.orElseThrow(() -> new EntityNotFoundException("User not found with id: " + user.getId()));

		managedUser.setName(user.getName());
		managedUser.setLastName(user.getLastName());
		managedUser.setEmail(user.getEmail());
		managedUser.setAge(user.getAge());

		if (user.getPassword() != null && !user.getPassword().trim().isEmpty()) {
			managedUser.setPassword(passwordEncoder.encode(user.getPassword()));
		}

		if (roleIds != null) {
			List<Role> roles = roleService.getRolesByIds(roleIds);
			managedUser.setRoles(new HashSet<>(roles));
		}

		userDao.updateUser(managedUser);
	}

	@Override
	@Transactional
	public void removeUser(Long id) {
		userDao.removeUser(id);
	}

	@Override
	@Transactional(readOnly = true)
	public User getUserById(Long id) {
		return userDao.getUserById(id)
				.orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
	}

	@Override
	@Transactional(readOnly = true)
	public List<User> getAllUsers() {

		return userDao.getAllUsers();
	}
}
