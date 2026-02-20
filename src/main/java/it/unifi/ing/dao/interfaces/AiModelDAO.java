package it.unifi.ing.dao.interfaces;

import it.unifi.ing.domain.AiModel;
import it.unifi.ing.domain.ModelStatus;

import java.util.List;

public interface AiModelDAO {
	void save(AiModel model);
	AiModel findById(int id);
	List<AiModel> findAll();
	List<AiModel> findByStatus(ModelStatus status);
	void update(AiModel model);
	void delete(int id);
}
