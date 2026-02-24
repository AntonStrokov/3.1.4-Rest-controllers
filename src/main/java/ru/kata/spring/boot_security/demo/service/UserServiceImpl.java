package ru.kata.spring.boot_security.demo.service;

import lombok.RequiredArgsConstructor;
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

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserDao userDao;
	private final PasswordEncoder passwordEncoder;
	private final RoleDao roleDao;

	@Override
	@Transactional
	public void addUser(User user) {
		user.setPassword(passwordEncoder.encode(user.getPassword()));

		// Обработка ролей: если пришли из формы - подгружаем полные объекты, если нет - даем USER
		if (user.getRoles() == null || user.getRoles().isEmpty()) {
			Role userRole = roleDao.getRoleByName("ROLE_USER").orElseThrow();
			user.setRoles(Collections.singleton(userRole));
		}
		else {
			// Подгружаем полные объекты ролей из БД по ID, которые пришил из чекбоксов
			user.setRoles(fetchRolesFromDb(user.getRoles()));
		}

		userDao.addUser(user);
	}

	@Override
	@Transactional
	public void updateUser(User user) {
		// 1. Находим "живой" объект в контексте Hibernate
		User managedUser = userDao.getUserById(user.getId())
				.orElseThrow(() -> new EntityNotFoundException("User not found"));

		// 2. Обновляем поля вручную (это гарантирует, что Hibernate увидит изменения)
		managedUser.setName(user.getName());
		managedUser.setEmail(user.getEmail());
		managedUser.setAge(user.getAge());

		// 3. Пароль: шифруем только если он пришел НЕ ПУСТОЙ
		if (user.getPassword() != null && !user.getPassword().trim().isEmpty()) {
			managedUser.setPassword(passwordEncoder.encode(user.getPassword()));
		}

		// 4. Роли: Просто перекладываем. Hibernate сам обновит таблицу users_roles
		if (user.getRoles() != null && !user.getRoles().isEmpty()) {
			managedUser.setRoles(user.getRoles());
		}

		// ВАЖНО: Никаких userDao.updateUser(managedUser) вызывать НЕ НУЖНО.
		// @Transactional в конце метода сам зафиксирует изменения в базе (Dirty Checking).
	}


	/**
	 * Вспомогательный метод, чтобы превратить "пустые" роли из формы (только с ID)
	 * в полноценные роли из базы данных.
	 */
	private Set<Role> fetchRolesFromDb(Set<Role> rolesFromForm) {
		return rolesFromForm.stream()
				.map(role -> roleDao.getRoleById(role.getId())) // Нужен метод findById в RoleDao
				.collect(Collectors.toSet());
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
