package it.unifi.ing.dao.memory;

import it.unifi.ing.domain.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class InMemorySessionDAOTest {

	private InMemorySessionDAO dao;
	private Developer dev;
	private AiModel model;
	private GPU gpu;

	@BeforeEach
	void setUp() {
		dao = new InMemorySessionDAO();
		dev = new Developer(1, "Dev", "dev@test.com", "pass");
		ModelProvider prov = new ModelProvider(2, "Prov", "prov@test.com", "pass");
		model = new AiModel(1, "TestModel", "Desc", 5.0, "s.bin", "c.json", prov);
		gpu = new GPU(1);
	}

	@Test
	void testSaveAndFindById() {
		Session s = new Session(1, dev, model, gpu);
		dao.save(s);
		assertNotNull(dao.findById(1));
	}

	@Test
	void testFindByUser() {
		Developer dev2 = new Developer(3, "Dev2", "dev2@test.com", "pass");
		dao.save(new Session(1, dev, model, gpu));
		dao.save(new Session(2, dev2, model, new GPU(2)));
		dao.save(new Session(3, dev, model, new GPU(3)));

		List<Session> sessions = dao.findByUser(1);
		assertEquals(2, sessions.size());
	}

	@Test
	void testFindAll() {
		dao.save(new Session(1, dev, model, gpu));
		dao.save(new Session(2, dev, model, new GPU(2)));
		assertEquals(2, dao.findAll().size());
	}

	@Test
	void testUpdate() {
		Session s = new Session(1, dev, model, gpu);
		dao.save(s);
		s.addTokens(100);
		dao.update(s);
		assertEquals(100, dao.findById(1).getTotalTokensUsed());
	}

	@Test
	void testDelete() {
		dao.save(new Session(1, dev, model, gpu));
		dao.delete(1);
		assertNull(dao.findById(1));
	}
}
