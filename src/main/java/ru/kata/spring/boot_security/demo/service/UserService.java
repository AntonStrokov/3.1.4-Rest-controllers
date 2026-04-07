package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.dto.UserDto;
import ru.kata.spring.boot_security.demo.exception.UserNotFoundException;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;

public interface UserService {
	List<UserDto> getAllUsersDto(); // Теперь возвращаем DTO
	UserDto getUserDtoById(Long id); // Теперь возвращаем DTO
	void addUser(UserDto userDto); // Принимаем DTO
	void updateUser(Long id, UserDto userDto); // ID и DTO
	void removeUser(Long id);
}

