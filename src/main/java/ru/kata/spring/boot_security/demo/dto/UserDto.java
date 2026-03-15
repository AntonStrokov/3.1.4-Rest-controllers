package ru.kata.spring.boot_security.demo.dto;

import lombok.Data;
import ru.kata.spring.boot_security.demo.model.Role;

import java.util.List;
import java.util.Set;

@Data
public class UserDto {
	private Long id;
	private String name;
	private String lastName;
	private int age;
	private String email;
	private Set<Role> roles;
	private List<Role> allRoles;

}