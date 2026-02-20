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
		assertEquals(gpu, session.getGpu());
		assertTrue(session.isActive());
		assertEquals(0, session.getUnbilledUsedTokens());
	}

	@Test
	void testAddTokens() {
		session.addTokens(50);
		assertEquals(50, session.getUnbilledUsedTokens());
		assertEquals(50, session.getTotalTokensUsed());
	}

	@Test
	void testResetTokens() {
		session.addTokens(50);
		session.resetTokens();
		assertEquals(0, session.getUnbilledUsedTokens());
		assertEquals(50, session.getTotalTokensUsed());
	}

	@Test
	void testCloseSession() {
		session.close();
		assertFalse(session.isActive());
		assertNotNull(session.getEndTimestamp());
	}

	@Test
	void testAddLog() {
		session.addLog("Test log entry");
		assertEquals(1, session.getInteractionLog().size());
	}

	@Test
	void testMultipleTokenAdditions() {
		session.addTokens(10);
		session.addTokens(20);
		assertEquals(30, session.getUnbilledUsedTokens());
		assertEquals(30, session.getTotalTokensUsed());
	}

	@Test
	void testAddTotalCost() {
		session.addTotalCost(1.5);
		session.addTotalCost(2.0);
		assertEquals(3.5, session.getTotalCost(), 0.001);
	}

	@Test
	void testLogImmutability() {
		session.addLog("Test");
		assertThrows(UnsupportedOperationException.class, () -> session.getInteractionLog().add("Fail"));
	}
}
