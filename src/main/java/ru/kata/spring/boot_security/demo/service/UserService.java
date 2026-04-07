package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.dto.UserDto;

import java.util.List;


public interface UserService {

	List<UserDto> getAllUsersDto();


	UserDto getUserDtoById(Long id);


	void addUser(UserDto userDto);


	void updateUser(Long id, UserDto userDto);


	void removeUser(Long id);
}
