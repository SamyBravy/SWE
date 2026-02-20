package it.unifi.ing.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class SessionTest {

	private Session session;
	private Developer developer;
	private AiModel model;
	private GPU gpu;

	@BeforeEach
	void setUp() {
		developer = new Developer(1, "Dev", "dev@test.com", "pass");
		developer.getWallet().addFunds(100.0);
		ModelProvider provider = new ModelProvider(2, "Prov", "prov@test.com", "pass");
		model = new AiModel(1, "TestModel", "Desc", 0.01, "s.bin", "c.json", provider);
		gpu = new GPU(1);
		session = new Session(1, developer, model, gpu);
	}

	@Test
	void testSessionCreation() {
		assertEquals(1, session.getId());
		assertEquals(developer, session.getDeveloper());
		assertEquals(model, session.getModel());
		assertEquals(1, session.getGpus().size());
		assertTrue(session.getGpus().contains(gpu));
		assertTrue(session.isActive());
		assertEquals(0, session.getTotalTokensUsed());
	}

	@Test
	void testAddTokens() {
		session.addUsedTokens(50);
		assertEquals(50, session.getTotalTokensUsed());
	}

	@Test
	void testCloseSession() {
		session.close();
		assertFalse(session.isActive());
	}

	@Test
	void testAddLog() {
		session.addLog("Test log entry");
		assertEquals(1, session.getInteractionLog().size());
	}

	@Test
	void testMultipleTokenAdditions() {
		session.addUsedTokens(10);
		session.addUsedTokens(20);
		assertEquals(30, session.getTotalTokensUsed());
	}

	@Test
	void testLogImmutability() {
		session.addLog("Test");
		assertThrows(UnsupportedOperationException.class, () -> session.getInteractionLog().add("Fail"));
	}
}
