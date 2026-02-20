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
	void testPublishModel() {
		modelService.publishModel(provider, "TestModel", "Desc", 0.01, "s.bin", "c.json");
		assertEquals(1, modelService.getAllModels().size());
	}

	@Test
	void testGetPendingModels() {
		modelService.publishModel(provider, "M1", "D1", 0.01, "s.bin", "c.json");
		assertEquals(1, modelService.getPendingModels().size());
	}

	@Test
	void testGetApprovedModels() {
		modelService.publishModel(provider, "M1", "D1", 0.01, "s.bin", "c.json");
		assertEquals(0, modelService.getApprovedModels().size());
	}

	@Test
	void testFindById() {
		modelService.publishModel(provider, "M1", "D1", 0.01, "s.bin", "c.json");
		AiModel model = modelService.findById(1);
		assertNotNull(model);
		assertEquals("M1", model.getName());
	}

	@Test
	void testFindByIdNonExistent() {
		assertNull(modelService.findById(999));
	}
}
