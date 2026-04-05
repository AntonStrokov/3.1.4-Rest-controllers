package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.exception.UserNotFoundException;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;

public interface UserService {

	List<User> getAllUsers();

	User getUserById(Long id) throws UserNotFoundException;

	void addUser(User user, List<Long> roleIds);

	void updateUser(User user, List<Long> roleIds);

	void removeUser(Long id);
}
