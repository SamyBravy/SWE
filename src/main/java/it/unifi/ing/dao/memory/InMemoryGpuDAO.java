package it.unifi.ing.dao.memory;

import it.unifi.ing.dao.interfaces.GpuDAO;
import it.unifi.ing.domain.GPU;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementazione in memoria del DAO GPU.
 * Usa una Map<Integer, GPU> come storage.
 */
public class InMemoryGpuDAO implements GpuDAO {

	private final Map<Integer, GPU> storage = new HashMap<>();

	@Override
	public void save(GPU gpu) {
		storage.put(gpu.getId(), gpu);
	}

	@Override
	public GPU findById(int id) {
		return storage.get(id);
	}

	@Override
	public List<GPU> findAll() {
		return new ArrayList<>(storage.values());
	}

	@Override
	public void delete(int id) {
		storage.remove(id);
	}
}
