package ru.kata.spring.boot_security.demo.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import ru.kata.spring.boot_security.demo.service.RoleService;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

	private final RoleRepository roleRepository;

	@Override
	public List<Role> getAllRoles() {
		return roleRepository.findAll();
	}

	@Override
	public Optional<Role> getRoleByName(String name) {
		return roleRepository.findByName(name);
	}

	@Override
	public List<Role> getRolesByIds(List<Long> ids) {
		return roleRepository.findAllById(ids);
	}

	@Override
	public Role save(Role role) {
		return roleRepository.save(role);
	}
}
