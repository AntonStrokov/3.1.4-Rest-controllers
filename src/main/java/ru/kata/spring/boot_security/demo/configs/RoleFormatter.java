package ru.kata.spring.boot_security.demo.configs;

import lombok.RequiredArgsConstructor;
import org.springframework.format.Formatter;
import org.springframework.stereotype.Component;
import ru.kata.spring.boot_security.demo.dao.RoleDao;
import ru.kata.spring.boot_security.demo.model.Role;

import java.text.ParseException;
import java.util.Locale;

@Component
@RequiredArgsConstructor
public class RoleFormatter implements Formatter<Role> {

	private final RoleDao roleDao;

	@Override
	public Role parse(String text, Locale locale) throws ParseException {
		// Когда Spring видит ID из чекбокса, он вызывает этот метод
		return roleDao.getRoleById(Long.parseLong(text));
	}

	@Override
	public String print(Role object, Locale locale) {
		return object.getId().toString();
	}
}
