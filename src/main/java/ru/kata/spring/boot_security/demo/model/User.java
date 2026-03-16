package ru.kata.spring.boot_security.demo.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.kata.spring.boot_security.demo.validation.OnCreate;
import ru.kata.spring.boot_security.demo.validation.OnUpdate;

import javax.persistence.*;
import javax.validation.constraints.*;
import javax.validation.groups.Default;
import java.util.Collection;
import java.util.Set;

@Entity
@Table(name = "users")
@Setter
@Getter
@NoArgsConstructor
public class User implements UserDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(message = "Имя не может быть пустым", groups = {OnCreate.class, OnUpdate.class, Default.class})
	@Size(min = 2, max = 30, message = "Имя должно быть от 2 до 30 символов", groups = {OnCreate.class, OnUpdate.class,
			Default.class})
	private String name;

	@NotBlank(message = "Фамилия не может быть пустой", groups = {OnCreate.class, OnUpdate.class, Default.class})
	@Size(min = 2, max = 30, message = "Фамилия должна быть от 2 до 30 символов", groups = {OnCreate.class,
			OnUpdate.class, Default.class})
	private String lastName;

	@NotBlank(message = "Email не может быть пустым", groups = {OnCreate.class, OnUpdate.class, Default.class})
	@Email(message = "Email должен быть корректным", groups = {OnCreate.class, OnUpdate.class, Default.class})
	private String email;

	@NotNull(message = "Возраст обязателен", groups = {OnCreate.class, OnUpdate.class, Default.class})
	@Min(value = 0, message = "Возраст не может быть отрицательным", groups = {OnCreate.class, OnUpdate.class,
			Default.class})
	@Max(value = 120, message = "Возраст не может быть больше 120", groups = {OnCreate.class, OnUpdate.class,
			Default.class})
	private Integer age;

	// Пароль обязателен только при создании (OnCreate)
	@NotBlank(message = "Пароль обязателен", groups = OnCreate.class)
	@Size(min = 3, max = 100, message = "Пароль должен быть от 3 до 100 символов", groups = OnCreate.class)
	private String password;


	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
			name = "users_roles",
			joinColumns = @JoinColumn(name = "user_id"),
			inverseJoinColumns = @JoinColumn(name = "role_id")
	)
	private Set<Role> roles;


	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return roles;
	}

	@Override
	public String getUsername() {
		return email;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}