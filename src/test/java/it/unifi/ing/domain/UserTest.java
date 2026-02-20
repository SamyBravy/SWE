package it.unifi.ing.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

	@Test
	void testDeveloperCreation() {
		Developer dev = new Developer(1, "John", "john@test.com", "pass");
		assertEquals(1, dev.getId());
		assertEquals("John", dev.getName());
		assertEquals("john@test.com", dev.getEmail());
		assertEquals("Developer", dev.getRole());
		assertNotNull(dev.getWallet());
	}

	@Test
	void testModelProviderCreation() {
		ModelProvider mp = new ModelProvider(2, "Provider", "prov@test.com", "pass");
		assertEquals("ModelProvider", mp.getRole());
	}

	@Test
	void testSupervisorCreation() {
		Supervisor sup = new Supervisor(3, "Admin", "admin@test.com", "pass");
		assertEquals("Supervisor", sup.getRole());
	}

	@Test
	void testLogin() {
		Developer dev = new Developer(1, "John", "john@test.com", "pass");
		assertTrue(dev.login("john@test.com", "pass"));
		assertFalse(dev.login("john@test.com", "wrong"));
		assertFalse(dev.login("wrong@test.com", "pass"));
	}

	@Test
	void testWalletOnlyOnDeveloper() {
		Developer dev = new Developer(1, "John", "john@test.com", "pass");
		assertNotNull(dev.getWallet());
		assertEquals(0.0, dev.getWallet().getBalance());
	}
}
