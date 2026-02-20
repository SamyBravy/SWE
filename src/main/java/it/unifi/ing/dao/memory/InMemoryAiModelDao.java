package it.unifi.ing.dao.memory;

import it.unifi.ing.dao.interfaces.AiModelDao;
import it.unifi.ing.domain.AiModel;
import it.unifi.ing.domain.ModelStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InMemoryAiModelDao implements AiModelDao {

	private final Map<Integer, AiModel> storage = new HashMap<>();

	@Override
	public void save(AiModel model) {
		storage.put(model.getId(), model);
	}

	@Override
	public AiModel findById(int id) {
		return storage.get(id);
	}

	@Override
	public List<AiModel> findAll() {
		return new ArrayList<>(storage.values());
	}

	@Override
	public List<AiModel> findByStatus(ModelStatus status) {
		return storage.values().stream()
				.filter(m -> m.getStatus() == status)
				.collect(Collectors.toList());
	}

	@Override
	public void update(AiModel model) {
		storage.put(model.getId(), model);
	}

	@Override
	public void delete(int id) {
		storage.remove(id);
	}
}
