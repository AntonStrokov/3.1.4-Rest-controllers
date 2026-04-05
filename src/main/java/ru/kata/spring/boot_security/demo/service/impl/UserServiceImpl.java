package ru.kata.spring.boot_security.demo.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.exception.UserNotFoundException;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.UserRepository;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final RoleService roleService;

	@Override
	@Transactional
	public void addUser(User user, List<Long> roleIds) {
		user.setPassword(passwordEncoder.encode(user.getPassword()));

		if (roleIds != null && !roleIds.isEmpty()) {
			user.setRoles(new HashSet<>(roleService.getRolesByIds(roleIds)));
		}
		else {
			roleService.getRoleByName("ROLE_USER")
					.ifPresent(r -> user.setRoles(Set.of(r)));
		}

		userRepository.save(user);
	}

	@Override
	@Transactional
	public void updateUser(User user, List<Long> roleIds) {

		User managedUser = userRepository.findById(user.getId())
				.orElseThrow(() -> new UserNotFoundException(user.getId()));

		managedUser.setName(user.getName());
		managedUser.setLastName(user.getLastName());
		managedUser.setEmail(user.getEmail());
		managedUser.setAge(user.getAge());

		if (user.getPassword() != null && !user.getPassword().trim().isEmpty()) {
			managedUser.setPassword(passwordEncoder.encode(user.getPassword()));
		}

		if (roleIds != null) {
			managedUser.setRoles(new HashSet<>(roleService.getRolesByIds(roleIds)));
		}

		userRepository.save(managedUser);
	}

	@Override
	@Transactional
	public void removeUser(Long id) {

		User user = userRepository.findById(id)
				.orElseThrow(() -> new UserNotFoundException(id));
		userRepository.delete(user);
	}

	@Override
	@Transactional(readOnly = true)
	public User getUserById(Long id) {
		return userRepository.findById(id)
				.orElseThrow(() -> new UserNotFoundException(id));
	}

	@Override
	@Transactional(readOnly = true)
	public List<User> getAllUsers() {
		return userRepository.findAll();
	}
}
