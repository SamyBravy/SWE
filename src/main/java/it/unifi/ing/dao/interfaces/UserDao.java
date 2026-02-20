package it.unifi.ing.dao.interfaces;

import it.unifi.ing.domain.User;

import java.util.List;

public interface UserDao extends GenericDao<User> {
	User findByEmail(String email);
}
