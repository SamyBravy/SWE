package it.unifi.ing.business.services;

import it.unifi.ing.dao.memory.InMemoryAiModelDao;
import it.unifi.ing.domain.AiModel;
import it.unifi.ing.domain.ModelProvider;
import it.unifi.ing.domain.ModelStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class ModelServiceTest {

	private ModelService modelService;
	private ModelProvider provider;

	@BeforeEach
	void setUp() {
		modelService = new ModelService(new InMemoryAiModelDao());
		provider = new ModelProvider(1, "Prov", "prov@test.com", "pass");
	}

	@Test
	void testPublishModelScenario() {
		modelService.publishModel(provider, "TestModel", "Desc", 0.01, "s.safetensors", "c.json");
		assertEquals(1, modelService.getAllModels().size());

		assertEquals(1, modelService.getPendingModels().size());
		assertEquals(0, modelService.getApprovedModels().size());

		AiModel model = modelService.findById(1);
		assertNotNull(model);
		assertEquals("TestModel", model.getName());
		assertEquals(ModelStatus.PENDING_REVIEW, model.getStatus());

		assertNull(modelService.findById(999));
	}
}
