package it.unifi.ing.business.services;

import it.unifi.ing.dao.memory.InMemoryModelloDAO;
import it.unifi.ing.domain.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ModelServiceTest {

	private ModelService modelService;
	private ModelProvider provider;

	@BeforeEach
	void setUp() {
		modelService = new ModelService(new InMemoryModelloDAO());
		provider = new ModelProvider(1, "Provider1", "prov@test.com", "pass");
	}

	@Test
	void testPublishModel() {
		modelService.publishModel(provider, "GPT-Test", "Un modello di test", 10.0, "s.bin", "c.json");

		List<Modello> pending = modelService.getPendingModels();
		assertEquals(1, pending.size());
		assertEquals("GPT-Test", pending.get(0).getNome());
		assertEquals(StatoModello.IN_ATTESA, pending.get(0).getStato());
	}

	@Test
	void testGetPendingModels() {
		modelService.publishModel(provider, "Model1", "Desc", 5.0, "s1", "j1");
		modelService.publishModel(provider, "Model2", "Desc", 5.0, "s2", "j2");

		assertEquals(2, modelService.getPendingModels().size());
	}

	@Test
	void testGetApprovedModels() {
		modelService.publishModel(provider, "Model1", "Desc", 5.0, "s1", "j1");
		assertEquals(0, modelService.getApprovedModels().size());
	}

	@Test
	void testGetAllModels() {
		modelService.publishModel(provider, "Model1", "Desc", 5.0, "s1", "j1");
		modelService.publishModel(provider, "Model2", "Desc", 5.0, "s2", "j2");

		assertEquals(2, modelService.getAllModels().size());
	}

	@Test
	void testFindById() {
		modelService.publishModel(provider, "Model1", "Desc", 5.0, "s1", "j1");

		Modello found = modelService.findById(1);
		assertNotNull(found);
		assertEquals("Model1", found.getNome());
	}
}
