package ru.kata.spring.boot_security.demo.configs;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.kata.spring.boot_security.demo.dao.RoleDao;
import ru.kata.spring.boot_security.demo.model.Role;

@Component
@RequiredArgsConstructor
public class RoleConverter implements Converter<String, Role> {

	private final RoleDao roleDao;

	@Override
	public Role convert(String roleId) {
		if (roleId == null || roleId.trim().isEmpty()) {
			return null;
		}
		try {
			Long id = Long.parseLong(roleId);
			return roleDao.getRoleById(id)
					.orElseThrow(() -> new IllegalArgumentException("Role not found with id: " + id));
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Invalid role id: " + roleId, e);
		}
	}
}
