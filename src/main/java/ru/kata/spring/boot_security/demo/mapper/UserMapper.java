package ru.kata.spring.boot_security.demo.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.kata.spring.boot_security.demo.dto.UserDto;
import ru.kata.spring.boot_security.demo.model.User;

@Mapper(componentModel = "spring", imports = {java.util.stream.Collectors.class})
public interface UserMapper {

	UserDto toDto(User user);

	@Mapping(target = "roles", ignore = true)
	User toEntity(UserDto dto);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "roles", ignore = true)
	@Mapping(target = "password", ignore = true)
	void updateEntityFromDto(UserDto dto, @MappingTarget User entity);
}

