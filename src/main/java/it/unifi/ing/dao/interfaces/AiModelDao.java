package it.unifi.ing.dao.interfaces;

import it.unifi.ing.domain.AiModel;
import it.unifi.ing.domain.ModelStatus;

import java.util.List;

public interface AiModelDao extends GenericDao<AiModel> {
	List<AiModel> findByStatus(ModelStatus status);
}
