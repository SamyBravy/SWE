package it.unifi.ing.dao.memory;

import it.unifi.ing.domain.AiModel;
import it.unifi.ing.domain.ModelProvider;
import it.unifi.ing.domain.ModelStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryAiModelDaoTest {

	private InMemoryAiModelDao dao;
	private ModelProvider provider;

	@BeforeEach
	void setUp() {
		dao = new InMemoryAiModelDao();
		provider = new ModelProvider(1, "Prov", "prov@test.com", "pass");
	}

	@Test
	void testSaveAndFindById() {
		AiModel model = AiModel.submitForReview(1, "TestModel", "Desc", 0.01, "s.safetensors", "c.json", provider);
		dao.save(model);
		assertNotNull(dao.findById(1));
	}

	@Test
	void testFindByStatus() {
		AiModel m1 = AiModel.submitForReview(1, "M1", "D1", 0.01, "s.safetensors", "c.json", provider);
		AiModel m2 = AiModel.submitForReview(2, "M2", "D2", 0.02, "s.safetensors", "c.json", provider);
		m2.setStatus(ModelStatus.APPROVED);
		dao.save(m1);
		dao.save(m2);
		assertEquals(1, dao.findByStatus(ModelStatus.PENDING_REVIEW).size());
		assertEquals(1, dao.findByStatus(ModelStatus.APPROVED).size());
	}

	@Test
	void testFindAll() {
		dao.save(AiModel.submitForReview(1, "M1", "D1", 0.01, "s.safetensors", "c.json", provider));
		dao.save(AiModel.submitForReview(2, "M2", "D2", 0.02, "s.safetensors", "c.json", provider));
		assertEquals(2, dao.findAll().size());
	}

	@Test
	void testDelete() {
		dao.save(AiModel.submitForReview(1, "M1", "D1", 0.01, "s.safetensors", "c.json", provider));
		dao.delete(1);
		assertNull(dao.findById(1));
	}

	@Test
	void testUpdate() {
		AiModel m = AiModel.submitForReview(1, "M1", "D1", 0.01, "s.safetensors", "c.json", provider);
		dao.save(m);
		m.setStatus(ModelStatus.APPROVED);
		dao.update(m);
		assertEquals(ModelStatus.APPROVED, dao.findById(1).getStatus());
	}
}
