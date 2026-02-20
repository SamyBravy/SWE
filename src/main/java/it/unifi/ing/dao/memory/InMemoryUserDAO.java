package it.unifi.ing.dao.memory;

import it.unifi.ing.dao.interfaces.UserDAO;
import it.unifi.ing.domain.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryUserDAO implements UserDAO {

	private final Map<Integer, User> storage = new HashMap<>();

	@Override
	public void save(User user) {
		storage.put(user.getId(), user);
	}

	@Override
	public User findById(int id) {
		return storage.get(id);
	}

	@Override
	public User findByEmail(String email) {
		return storage.values().stream()
				.filter(u -> u.getEmail().equals(email))
				.findFirst()
				.orElse(null);
	}

	@Override
	public List<User> findAll() {
		return new ArrayList<>(storage.values());
	}

	@Override
	public void delete(int id) {
		storage.remove(id);
	}
}
