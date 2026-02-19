package it.unifi.ing.dao.memory;

import it.unifi.ing.dao.interfaces.ModelloDAO;
import it.unifi.ing.domain.Modello;
import it.unifi.ing.domain.StatoModello;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementazione in memoria del DAO Modello.
 * Usa una Map<Integer, Modello> come storage.
 */
public class InMemoryModelloDAO implements ModelloDAO {

	private final Map<Integer, Modello> storage = new HashMap<>();

	@Override
	public void save(Modello modello) {
		storage.put(modello.getId(), modello);
	}

	@Override
	public Modello findById(int id) {
		return storage.get(id);
	}

	@Override
	public List<Modello> findAll() {
		return new ArrayList<>(storage.values());
	}

	@Override
	public List<Modello> findByStato(StatoModello stato) {
		return storage.values().stream()
				.filter(m -> m.getStato() == stato)
				.collect(Collectors.toList());
	}

	@Override
	public void update(Modello modello) {
		storage.put(modello.getId(), modello);
	}

	@Override
	public void delete(int id) {
		storage.remove(id);
	}
}
