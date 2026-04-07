package ru.kata.spring.boot_security.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;          // ← чтобы скрыть пароль в ответе
import lombok.Data;
import ru.kata.spring.boot_security.demo.validation.OnCreate;
import ru.kata.spring.boot_security.demo.validation.OnUpdate;

import javax.validation.constraints.*;
import javax.validation.groups.Default;
import java.util.List;
import java.util.Set;

@Data
public class UserDto {


	private Long id;

	@NotBlank(message = "Имя не может быть пустым",
			groups = {OnCreate.class, OnUpdate.class, Default.class})
	@Size(min = 2, max = 30,
			message = "Имя должно быть от 2 до 30 символов",
			groups = {OnCreate.class, OnUpdate.class, Default.class})
	private String name;

	@NotBlank(message = "Фамилия не может быть пустой",
			groups = {OnCreate.class, OnUpdate.class, Default.class})
	@Size(min = 2, max = 30,
			message = "Фамилия должна быть от 2 до 30 символов",
			groups = {OnCreate.class, OnUpdate.class, Default.class})
	private String lastName;


	@NotNull(message = "Возраст обязателен",
			groups = {OnCreate.class, OnUpdate.class, Default.class})
	@Min(value = 0, message = "Возраст не может быть отрицательным",
			groups = {OnCreate.class, OnUpdate.class, Default.class})
	@Max(value = 120, message = "Возраст не может быть больше 120",
			groups = {OnCreate.class, OnUpdate.class, Default.class})
	private Integer age;


	@NotBlank(message = "Email не может быть пустым",
			groups = {OnCreate.class, OnUpdate.class, Default.class})
	@Email(message = "Email должен быть корректным",
			groups = {OnCreate.class, OnUpdate.class, Default.class})
	private String email;


	@NotBlank(message = "Пароль обязателен", groups = OnCreate.class)
	@Size(min = 3, max = 100,
			message = "Пароль должен быть от 3 до 100 символов",
			groups = OnCreate.class)
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String password;


	private Set<?> roles;

	private List<Long> roleIds;
}
