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
		AiModel model = new AiModel(1, "TestModel", "Desc", 0.01, "s.bin", "c.json", provider);
		dao.save(model);
		assertNotNull(dao.findById(1));
	}

	@Test
	void testFindByStatus() {
		AiModel m1 = new AiModel(1, "M1", "D1", 0.01, "s.bin", "c.json", provider);
		AiModel m2 = new AiModel(2, "M2", "D2", 0.02, "s.bin", "c.json", provider);
		m2.setStatus(ModelStatus.APPROVED);
		dao.save(m1);
		dao.save(m2);
		assertEquals(1, dao.findByStatus(ModelStatus.PENDING_REVIEW).size());
		assertEquals(1, dao.findByStatus(ModelStatus.APPROVED).size());
	}

	@Test
	void testFindAll() {
		dao.save(new AiModel(1, "M1", "D1", 0.01, "s.bin", "c.json", provider));
		dao.save(new AiModel(2, "M2", "D2", 0.02, "s.bin", "c.json", provider));
		assertEquals(2, dao.findAll().size());
	}

	@Test
	void testDelete() {
		dao.save(new AiModel(1, "M1", "D1", 0.01, "s.bin", "c.json", provider));
		dao.delete(1);
		assertNull(dao.findById(1));
	}

	@Test
	void testUpdate() {
		AiModel m = new AiModel(1, "M1", "D1", 0.01, "s.bin", "c.json", provider);
		dao.save(m);
		m.setStatus(ModelStatus.APPROVED);
		dao.update(m);
		assertEquals(ModelStatus.APPROVED, dao.findById(1).getStatus());
	}
}
