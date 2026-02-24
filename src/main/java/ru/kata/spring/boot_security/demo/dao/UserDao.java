package ru.kata.spring.boot_security.demo.dao;

import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {
	void addUser(User user);
	void updateUser(User user);
	void removeUser(Long id);
	Optional<User> getUserById(Long id);
	List<User> getAllUsers();
}