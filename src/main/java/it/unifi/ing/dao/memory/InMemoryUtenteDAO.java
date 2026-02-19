package it.unifi.ing.dao.memory;

import it.unifi.ing.dao.interfaces.UtenteDAO;
import it.unifi.ing.domain.Utente;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementazione in memoria del DAO Utente.
 * Usa una Map<Integer, Utente> come storage.
 */
public class InMemoryUtenteDAO implements UtenteDAO {

	private final Map<Integer, Utente> storage = new HashMap<>();

	@Override
	public void save(Utente utente) {
		storage.put(utente.getId(), utente);
	}

	@Override
	public Utente findById(int id) {
		return storage.get(id);
	}

	@Override
	public Utente findByEmail(String email) {
		return storage.values().stream()
				.filter(u -> u.getEmail().equals(email))
				.findFirst()
				.orElse(null);
	}

	@Override
	public List<Utente> findAll() {
		return new ArrayList<>(storage.values());
	}

	@Override
	public void delete(int id) {
		storage.remove(id);
	}
}
