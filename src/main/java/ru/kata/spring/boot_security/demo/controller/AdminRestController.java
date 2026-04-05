package ru.kata.spring.boot_security.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.dto.UserDto;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;
import ru.kata.spring.boot_security.demo.validation.OnCreate;
import ru.kata.spring.boot_security.demo.validation.OnUpdate;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminRestController {

	private final UserService userService;
	private final RoleService roleService;

	private UserDto convertToDto(User user) {
		UserDto dto = new UserDto();
		dto.setId(user.getId());
		dto.setName(user.getName());
		dto.setLastName(user.getLastName());
		dto.setAge(user.getAge());
		dto.setEmail(user.getEmail());
		dto.setRoles(user.getRoles());
		dto.setRoleIds(user.getRoles().stream().map(Role::getId).collect(Collectors.toList()));
		return dto;
	}

	@GetMapping("/users")
	public ResponseEntity<List<UserDto>> getAllUsers() {
		List<UserDto> users = userService.getAllUsers().stream()
				.map(this::convertToDto)
				.collect(Collectors.toList());
		return ResponseEntity.ok(users);
	}

	@GetMapping("/users/{id}")
	public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
		User user = userService.getUserById(id);
		return ResponseEntity.ok(convertToDto(user));
	}

	@GetMapping("/roles")
	public ResponseEntity<List<Role>> getAllRoles() {
		return ResponseEntity.ok(roleService.getAllRoles());
	}

	@PostMapping("/users")
	public ResponseEntity<?> createUser(@Validated(OnCreate.class) @RequestBody UserDto userDto) {
		User user = new User();
		user.setName(userDto.getName());
		user.setLastName(userDto.getLastName());
		user.setAge(userDto.getAge());
		user.setEmail(userDto.getEmail());
		user.setPassword(userDto.getPassword());
		userService.addUser(user, userDto.getRoleIds());
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@PutMapping("/users/{id}")
	public ResponseEntity<?> updateUser(@PathVariable Long id,
	                                    @Validated(OnUpdate.class) @RequestBody UserDto userDto) {
		User existingUser = userService.getUserById(id);
		existingUser.setName(userDto.getName());
		existingUser.setLastName(userDto.getLastName());
		existingUser.setAge(userDto.getAge());
		existingUser.setEmail(userDto.getEmail());
		if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
			existingUser.setPassword(userDto.getPassword());
		}
		userService.updateUser(existingUser, userDto.getRoleIds());
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/users/{id}")
	public ResponseEntity<?> deleteUser(@PathVariable Long id) {
		userService.removeUser(id);
		return ResponseEntity.noContent().build();
	}
}