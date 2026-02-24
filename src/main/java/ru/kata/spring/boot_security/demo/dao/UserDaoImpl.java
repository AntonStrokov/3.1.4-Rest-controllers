package ru.kata.spring.boot_security.demo.dao;

import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.model.User;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserDaoImpl implements UserDao {

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	@Transactional
	public void addUser(User user) {
		entityManager.persist(user);
	}

	@Override
	@Transactional
	public void updateUser(User user) {
		entityManager.merge(user);
	}

	@Override
	@Transactional
	public void removeUser(Long id) {
		entityManager.createQuery("DELETE FROM User u WHERE u.id = :id")
				.setParameter("id", id)
				.executeUpdate();
	}

	@Override
	@Transactional
	public Optional<User> getUserById(Long id) {
		return Optional.ofNullable(entityManager.find(User.class, id));
	}

	@Override
	@Transactional
	public List<User> getAllUsers() {
		return entityManager.createQuery("from User", User.class).getResultList();
	}
}
