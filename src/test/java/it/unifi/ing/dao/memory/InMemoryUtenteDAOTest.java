package it.unifi.ing.dao.memory;

import it.unifi.ing.domain.Developer;
import it.unifi.ing.domain.ModelProvider;
import it.unifi.ing.domain.Supervisor;
import it.unifi.ing.domain.Utente;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryUtenteDAOTest {

	private InMemoryUtenteDAO dao;

	@BeforeEach
	void setUp() {
		dao = new InMemoryUtenteDAO();
	}

	@Test
	void testSaveAndFindById() {
		Developer dev = new Developer(1, "Mario", "mario@test.com", "pass");
		dao.save(dev);
		Utente found = dao.findById(1);
		assertNotNull(found);
		assertEquals("Mario", found.getNome());
	}

	@Test
	void testFindByEmail() {
		Developer dev = new Developer(1, "Mario", "mario@test.com", "pass");
		dao.save(dev);
		Utente found = dao.findByEmail("mario@test.com");
		assertNotNull(found);
		assertEquals(1, found.getId());
	}

	@Test
	void testFindByEmailNotFound() {
		assertNull(dao.findByEmail("nonexistent@test.com"));
	}

	@Test
	void testFindAll() {
		dao.save(new Developer(1, "Dev1", "dev1@test.com", "pass"));
		dao.save(new ModelProvider(2, "Prov1", "prov1@test.com", "pass"));
		dao.save(new Supervisor(3, "Sup1", "sup1@test.com", "pass"));
		assertEquals(3, dao.findAll().size());
	}

	@Test
	void testDelete() {
		dao.save(new Developer(1, "Dev1", "dev1@test.com", "pass"));
		dao.delete(1);
		assertNull(dao.findById(1));
	}

	@Test
	void testFindByIdNotFound() {
		assertNull(dao.findById(999));
	}
}
