package it.unifi.ing.dao.memory;

import it.unifi.ing.dao.interfaces.SessionDAO;
import it.unifi.ing.domain.Session;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InMemorySessionDAO implements SessionDAO {

	private final Map<Integer, Session> storage = new HashMap<>();

	@Override
	public void save(Session session) {
		storage.put(session.getId(), session);
	}

	@Override
	public Session findById(int id) {
		return storage.get(id);
	}

	@Override
	public List<Session> findByUser(int userId) {
		return storage.values().stream()
				.filter(s -> s.getDeveloper().getId() == userId)
				.collect(Collectors.toList());
	}

	@Override
	public List<Session> findAll() {
		return new ArrayList<>(storage.values());
	}

	@Override
	public void update(Session session) {
		storage.put(session.getId(), session);
	}

	@Override
	public List<Session> findActiveSessions() {
		return storage.values().stream()
				.filter(Session::isActive)
				.collect(Collectors.toList());
	}

	@Override
	public void delete(int id) {
		storage.remove(id);
	}
}
