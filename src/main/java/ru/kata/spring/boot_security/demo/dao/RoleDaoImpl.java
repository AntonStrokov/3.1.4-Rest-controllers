package ru.kata.spring.boot_security.demo.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.model.Role;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RoleDaoImpl implements RoleDao {

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	@Transactional
	public void addRole(Role role) {
		entityManager.persist(role);
	}

	@Override
	@Transactional
	public Optional<Role> getRoleByName(String name) {
		List<Role> roles = entityManager.createQuery("FROM Role r WHERE r.name = :name", Role.class)
				.setParameter("name", name)
				.getResultList();
		return roles.stream().findFirst();
	}

	@Override
	@Transactional
	public List<Role> getAllRoles() {
		return entityManager.createQuery("FROM Role", Role.class).getResultList();
	}

	@Override
	public Role getRoleById(Long id) {
		return entityManager.find(Role.class, id);
	}
}

