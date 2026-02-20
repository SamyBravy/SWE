package it.unifi.ing.dao.interfaces;

import it.unifi.ing.domain.User;

import java.util.List;

public interface UserDAO {
	void save(User user);
	User findById(int id);
	User findByEmail(String email);
	List<User> findAll();
	void delete(int id);
}
