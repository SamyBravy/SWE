package it.unifi.ing.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UtenteTest {

	@Test
	void testDeveloperCreazione() {
		Developer dev = new Developer(1, "Mario", "mario@test.com", "pass123");
		assertEquals(1, dev.getId());
		assertEquals("Mario", dev.getNome());
		assertEquals("mario@test.com", dev.getEmail());
		assertEquals("pass123", dev.getPassword());
		assertEquals("Developer", dev.getRuolo());
		assertNotNull(dev.getWallet());
		assertEquals(0.0, dev.getWallet().getSaldo());
	}

	@Test
	void testModelProviderCreazione() {
		ModelProvider mp = new ModelProvider(2, "Luca", "luca@test.com", "pass456");
		assertEquals("ModelProvider", mp.getRuolo());
		assertEquals("Luca", mp.getNome());
	}

	@Test
	void testSupervisorCreazione() {
		Supervisor sup = new Supervisor(3, "Anna", "anna@test.com", "pass789");
		assertEquals("Supervisor", sup.getRuolo());
		assertEquals("Anna", sup.getNome());
	}

	@Test
	void testSetters() {
		Developer dev = new Developer(1, "Mario", "mario@test.com", "pass123");
		dev.setNome("Mario Rossi");
		dev.setEmail("mario.rossi@test.com");
		dev.setPassword("newpass");

		assertEquals("Mario Rossi", dev.getNome());
		assertEquals("mario.rossi@test.com", dev.getEmail());
		assertEquals("newpass", dev.getPassword());
	}

	@Test
	void testToString() {
		Developer dev = new Developer(1, "Mario", "mario@test.com", "pass123");
		String str = dev.toString();
		assertTrue(str.contains("Developer"));
		assertTrue(str.contains("Mario"));
	}
}
