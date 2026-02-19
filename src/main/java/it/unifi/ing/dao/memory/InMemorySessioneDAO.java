package it.unifi.ing.dao.memory;

import it.unifi.ing.dao.interfaces.SessioneDAO;
import it.unifi.ing.domain.Sessione;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementazione in memoria del DAO Sessione.
 * Usa una Map<Integer, Sessione> come storage.
 */
public class InMemorySessioneDAO implements SessioneDAO {

	private final Map<Integer, Sessione> storage = new HashMap<>();

	@Override
	public void save(Sessione sessione) {
		storage.put(sessione.getId(), sessione);
	}

	@Override
	public Sessione findById(int id) {
		return storage.get(id);
	}

	@Override
	public List<Sessione> findByUtente(int utenteId) {
		return storage.values().stream()
				.filter(s -> s.getUtente().getId() == utenteId)
				.collect(Collectors.toList());
	}

	@Override
	public List<Sessione> findAll() {
		return new ArrayList<>(storage.values());
	}

	@Override
	public void update(Sessione sessione) {
		storage.put(sessione.getId(), sessione);
	}

	@Override
	public List<Sessione> findActiveSessions() {
		return storage.values().stream()
				.filter(Sessione::isAttiva)
				.collect(Collectors.toList());
	}

	@Override
	public void delete(int id) {
		storage.remove(id);
	}
}
