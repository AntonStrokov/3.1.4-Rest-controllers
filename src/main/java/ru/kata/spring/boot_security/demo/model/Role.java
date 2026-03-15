package ru.kata.spring.boot_security.demo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;

@Entity
@Table(name = "roles")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor // Полезно иметь
@org.springframework.transaction.annotation.Transactional // Это тут не нужно, удали если есть
public class Role implements GrantedAuthority {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true)
	private String name;

	public Role(Long id) {
		this.id = id;
	}

	// Добавь эти методы (или используй @EqualsAndHashCode(onlyExplicitlyIncluded = true) на классе)
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Role role = (Role) o;
		return java.util.Objects.equals(id, role.id) &&
				java.util.Objects.equals(name, role.name);
	}

	@Override
	public int hashCode() {
		return java.util.Objects.hash(id, name);
	}

	@Override
	public String getAuthority() {
		return name;
	}
}