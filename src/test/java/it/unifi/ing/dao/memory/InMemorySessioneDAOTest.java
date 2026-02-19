package it.unifi.ing.dao.memory;

import it.unifi.ing.domain.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemorySessioneDAOTest {

	private InMemorySessioneDAO dao;
	private Developer dev;
	private Modello modello;
	private GPU gpu;

	@BeforeEach
	void setUp() {
		dao = new InMemorySessioneDAO();
		dev = new Developer(1, "Dev", "dev@test.com", "pass");
		ModelProvider prov = new ModelProvider(2, "Prov", "prov@test.com", "pass");
		modello = new Modello(1, "TestModel", "Desc", 5.0, "s.bin", "c.json", prov);
		gpu = new GPU(1);
	}

	@Test
	void testSaveAndFindById() {
		Sessione s = new Sessione(1, dev, modello, gpu);
		dao.save(s);
		assertNotNull(dao.findById(1));
	}

	@Test
	void testFindByUtente() {
		Developer dev2 = new Developer(3, "Dev2", "dev2@test.com", "pass");
		dao.save(new Sessione(1, dev, modello, gpu));
		dao.save(new Sessione(2, dev2, modello, new GPU(2)));
		dao.save(new Sessione(3, dev, modello, new GPU(3)));

		List<Sessione> sessioni = dao.findByUtente(1);
		assertEquals(2, sessioni.size());
	}

	@Test
	void testFindAll() {
		dao.save(new Sessione(1, dev, modello, gpu));
		dao.save(new Sessione(2, dev, modello, new GPU(2)));
		assertEquals(2, dao.findAll().size());
	}

	@Test
	void testUpdate() {
		Sessione s = new Sessione(1, dev, modello, gpu);
		dao.save(s);
		s.addTokens(100);
		dao.update(s);
		assertEquals(100, dao.findById(1).getTokensUsed());
	}

	@Test
	void testDelete() {
		dao.save(new Sessione(1, dev, modello, gpu));
		dao.delete(1);
		assertNull(dao.findById(1));
	}
}
