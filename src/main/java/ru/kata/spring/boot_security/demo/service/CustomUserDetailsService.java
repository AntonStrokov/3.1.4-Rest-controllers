package ru.kata.spring.boot_security.demo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.dao.UserDao;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

	private final UserDao userDao;

	@Override
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		log.info("Loading user details: email={}", email);

		User user = userDao.getAllUsers().stream()
				.filter(u -> u.getEmail().equals(email))
				.findFirst()
				.orElseThrow(() -> {
					log.warn("User not found: email={}", email);
					return new UsernameNotFoundException("User not found: " + email);
				});

		log.info("User loaded successfully: id={}, email={}, roles={}",
				user.getId(), user.getEmail(),
				user.getRoles().stream().map(Role::getName).collect(Collectors.toList()));

		return user;
	}
}