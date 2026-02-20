package it.unifi.ing.business.services;

import it.unifi.ing.dao.interfaces.UserDao;
import it.unifi.ing.domain.Developer;
import it.unifi.ing.domain.ModelProvider;
import it.unifi.ing.domain.Supervisor;
import it.unifi.ing.domain.User;

/**
 * Service for user authentication and registration.
 */
public class AuthService {

	private final UserDao userDao;
	private int nextId;

	public AuthService(UserDao userDao) {
		this.userDao = userDao;
		this.nextId = 1;
	}

	public User login(String email, String password) {
		User user = userDao.findByEmail(email);
		if (user != null && user.login(email, password)) {
			return user;
		}
		return null;
	}

	public User register(String name, String email, String password, String role) {
		if (userDao.findByEmail(email) != null) {
			return null;
		}

		User user;
		switch (role.toLowerCase()) {
			case "developer":
				user = new Developer(nextId++, name, email, password);
				break;
			case "modelprovider":
				user = new ModelProvider(nextId++, name, email, password);
				break;
			case "supervisor":
				user = new Supervisor(nextId++, name, email, password);
				break;
			default:
				return null;
		}

		userDao.save(user);
		return user;
	}
}
