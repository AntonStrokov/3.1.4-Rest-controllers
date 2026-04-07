package ru.kata.spring.boot_security.demo.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.dto.UserDto;
import ru.kata.spring.boot_security.demo.exception.UserNotFoundException;
import ru.kata.spring.boot_security.demo.mapper.UserMapper;
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
	private final UserMapper userMapper;

	@Override
	@Transactional(readOnly = true)
	public List<UserDto> getAllUsersDto() {
		return userRepository.findAll().stream()
				.map(userMapper::toDto)
				.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public UserDto getUserDtoById(Long id) {
		User user = userRepository.findById(id)
				.orElseThrow(() -> new UserNotFoundException(id));
		return userMapper.toDto(user);
	}

	@Override
	@Transactional
	public void addUser(UserDto userDto) {
		User user = userMapper.toEntity(userDto);

		user.setPassword(passwordEncoder.encode(userDto.getPassword()));

		if (userDto.getRoleIds() != null && !userDto.getRoleIds().isEmpty()) {
			user.setRoles(new HashSet<>(roleService.getRolesByIds(userDto.getRoleIds())));
		}
		else {
			roleService.getRoleByName("ROLE_USER")
					.ifPresent(r -> user.setRoles(Set.of(r)));
		}

		userRepository.save(user);
	}

	@Override
	@Transactional
	public void updateUser(Long id, UserDto userDto) {
		User managedUser = userRepository.findById(id)
				.orElseThrow(() -> new UserNotFoundException(id));

		userMapper.updateEntityFromDto(userDto, managedUser);

		if (userDto.getPassword() != null && !userDto.getPassword().isBlank()) {
			managedUser.setPassword(passwordEncoder.encode(userDto.getPassword()));
		}

		if (userDto.getRoleIds() != null) {
			managedUser.setRoles(new HashSet<>(roleService.getRolesByIds(userDto.getRoleIds())));
		}

		userRepository.save(managedUser);
	}

	@Override
	@Transactional
	public void removeUser(Long id) {
		if (userRepository.existsById(id)) {
			userRepository.deleteById(id);
		}
	}
}
