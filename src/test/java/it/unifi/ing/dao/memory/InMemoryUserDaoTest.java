package it.unifi.ing.dao.memory;

import it.unifi.ing.domain.Developer;
import it.unifi.ing.domain.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryUserDaoTest {

	private InMemoryUserDao dao;

	@BeforeEach
	void setUp() {
		dao = new InMemoryUserDao();
	}

	@Test
	void testSaveAndFindById() {
		User user = new Developer(1, "Dev", "dev@test.com", "pass");
		dao.save(user);
		assertNotNull(dao.findById(1));
	}

	@Test
	void testFindByEmail() {
		User user = new Developer(1, "Dev", "dev@test.com", "pass");
		dao.save(user);
		assertNotNull(dao.findByEmail("dev@test.com"));
		assertNull(dao.findByEmail("nonexistent@test.com"));
	}

	@Test
	void testFindAll() {
		dao.save(new Developer(1, "Dev1", "d1@test.com", "pass"));
		dao.save(new Developer(2, "Dev2", "d2@test.com", "pass"));
		dao.save(new Developer(3, "Dev3", "d3@test.com", "pass"));
		assertEquals(3, dao.findAll().size());
	}

	@Test
	void testDelete() {
		dao.save(new Developer(1, "Dev", "dev@test.com", "pass"));
		dao.delete(1);
		assertNull(dao.findById(1));
	}

	@Test
	void testFindByIdNonExistent() {
		assertNull(dao.findById(999));
	}

	@Test
	void testUpdateUser() {
		Developer dev = new Developer(1, "Dev", "dev@test.com", "pass");
		dao.save(dev);
		dev.setName("Updated");
		dao.save(dev);
		assertEquals("Updated", dao.findById(1).getName());
	}
}
