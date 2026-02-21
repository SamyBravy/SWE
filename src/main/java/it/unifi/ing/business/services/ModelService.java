package it.unifi.ing.business.services;

import it.unifi.ing.dao.interfaces.AiModelDao;
import it.unifi.ing.domain.AiModel;
import it.unifi.ing.domain.ModelProvider;
import it.unifi.ing.domain.ModelStatus;

import java.util.List;

public class ModelService {

	private final AiModelDao modelDao;
	private int nextId;

	public ModelService(AiModelDao modelDao) {
		this.modelDao = modelDao;
		this.nextId = 1;
	}

	public void publishModel(ModelProvider provider, String name, String desc,
			double cost, String safetensors, String json) {
		AiModel model = AiModel.submitForReview(nextId++, name, desc, cost, safetensors, json, provider);
		modelDao.save(model);
	}

	public List<AiModel> getPendingModels() {
		return modelDao.findByStatus(ModelStatus.PENDING_REVIEW);
	}

	public List<AiModel> getApprovedModels() {
		return modelDao.findByStatus(ModelStatus.APPROVED);
	}

	public List<AiModel> getBlockedModels() {
		return modelDao.findByStatus(ModelStatus.BLOCKED);
	}

	public List<AiModel> getAllModels() {
		return modelDao.findAll();
	}

	public AiModel findById(int id) {
		return modelDao.findById(id);
	}
}
