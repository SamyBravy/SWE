package it.unifi.ing.dao.memory;

import it.unifi.ing.dao.interfaces.ReclamoDAO;
import it.unifi.ing.domain.Reclamo;
import it.unifi.ing.domain.StatoReclamo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementazione in memoria del DAO Reclamo.
 * Usa una Map<Integer, Reclamo> come storage.
 */
public class InMemoryReclamoDAO implements ReclamoDAO {

    private final Map<Integer, Reclamo> storage = new HashMap<>();

    @Override
    public void save(Reclamo reclamo) {
        storage.put(reclamo.getId(), reclamo);
    }

    @Override
    public Reclamo findById(int id) {
        return storage.get(id);
    }

    @Override
    public List<Reclamo> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public List<Reclamo> findByStato(StatoReclamo stato) {
        return storage.values().stream()
                .filter(r -> r.getStato() == stato)
                .collect(Collectors.toList());
    }

    @Override
    public void update(Reclamo reclamo) {
        storage.put(reclamo.getId(), reclamo);
    }

    @Override
    public void delete(int id) {
        storage.remove(id);
    }
}
