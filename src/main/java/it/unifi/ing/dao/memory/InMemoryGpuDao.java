package it.unifi.ing.dao.memory;

import it.unifi.ing.dao.interfaces.GpuDao;
import it.unifi.ing.domain.GPU;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryGpuDao implements GpuDao {

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
        public void update(GPU gpu) {
                storage.put(gpu.getId(), gpu);
        }

        @Override
        public void delete(int id) {
		storage.remove(id);
	}
}
