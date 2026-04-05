package ru.kata.spring.boot_security.demo.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.kata.spring.boot_security.demo.dto.UserDto;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.model.Role;

import java.util.List;

@Mapper(componentModel = "spring",
		imports = {java.util.stream.Collectors.class})
public interface UserMapper {

	@Mapping(target = "roleIds",
			expression = "java(user.getRoles().stream()" +
					".map(Role::getId)" +
					".collect(Collectors.toList()))")
	UserDto toDto(User user);

	@Mapping(target = "roles", ignore = true)
	User toEntity(UserDto dto);
}
