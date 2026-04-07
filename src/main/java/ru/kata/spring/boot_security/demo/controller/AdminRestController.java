package ru.kata.spring.boot_security.demo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.dto.UserDto;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;
import ru.kata.spring.boot_security.demo.validation.OnCreate;
import ru.kata.spring.boot_security.demo.validation.OnUpdate;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminRestController {

	private final UserService userService;
	private final RoleService roleService;

	@GetMapping("/users")
	public ResponseEntity<List<UserDto>> getAllUsers() {
		return ResponseEntity.ok(userService.getAllUsersDto());
	}

	@GetMapping("/users/{id}")
	public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
		return ResponseEntity.ok(userService.getUserDtoById(id));
	}

	@PostMapping("/users")
	public ResponseEntity<Void> createUser(
			@Validated(OnCreate.class) @RequestBody UserDto userDto) {
		userService.addUser(userDto);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@PutMapping("/users/{id}")
	public ResponseEntity<Void> updateUser(
			@PathVariable Long id,
			@Validated(OnUpdate.class) @RequestBody UserDto userDto) {
		userService.updateUser(id, userDto);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/users/{id}")
	public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
		userService.removeUser(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/roles")
	public ResponseEntity<List<Role>> getAllRoles() {
		return ResponseEntity.ok(roleService.getAllRoles());
	}
}
