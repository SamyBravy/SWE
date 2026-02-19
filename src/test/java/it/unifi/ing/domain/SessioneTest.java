package it.unifi.ing.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SessioneTest {

	@Test
	void testCreazioneSessione() {
		Developer dev = new Developer(1, "Dev", "dev@test.com", "pass");
		ModelProvider prov = new ModelProvider(2, "Prov", "prov@test.com", "pass");
		Modello modello = new Modello(1, "TestModel", "Desc", 5.0, "s.bin", "c.json", prov);
		GPU gpu = new GPU(1);

		Sessione sessione = new Sessione(1, dev, modello, gpu);

		assertEquals(1, sessione.getId());
		assertEquals(dev, sessione.getUtente());
		assertEquals(modello, sessione.getModello());
		assertEquals(gpu, sessione.getGpu());
		assertTrue(sessione.isAttiva());
		assertNotNull(sessione.getInizio());
		assertNull(sessione.getFine());
		assertEquals(0, sessione.getTokensUsed());
	}

	@Test
	void testAddTokens() {
		Developer dev = new Developer(1, "Dev", "dev@test.com", "pass");
		ModelProvider prov = new ModelProvider(2, "Prov", "prov@test.com", "pass");
		Modello modello = new Modello(1, "TestModel", "Desc", 5.0, "s.bin", "c.json", prov);
		GPU gpu = new GPU(1);

		Sessione sessione = new Sessione(1, dev, modello, gpu);
		sessione.addTokens(100);
		assertEquals(100, sessione.getTokensUsed());
		sessione.addTokens(50);
		assertEquals(150, sessione.getTokensUsed());
	}

	@Test
	void testChiudiSessione() {
		Developer dev = new Developer(1, "Dev", "dev@test.com", "pass");
		ModelProvider prov = new ModelProvider(2, "Prov", "prov@test.com", "pass");
		Modello modello = new Modello(1, "TestModel", "Desc", 5.0, "s.bin", "c.json", prov);
		GPU gpu = new GPU(1);

		Sessione sessione = new Sessione(1, dev, modello, gpu);
		sessione.chiudi();

		assertFalse(sessione.isAttiva());
		assertNotNull(sessione.getFine());
	}

	@Test
	void testGetLogs() {
		Developer dev = new Developer(1, "Dev", "dev@test.com", "pass");
		ModelProvider prov = new ModelProvider(2, "Prov", "prov@test.com", "pass");
		Modello modello = new Modello(1, "TestModel", "Desc", 5.0, "s.bin", "c.json", prov);
		GPU gpu = new GPU(1);

		Sessione sessione = new Sessione(1, dev, modello, gpu);
		sessione.addLog("log1");
		sessione.addLog("log2");

		assertEquals(2, sessione.getInteractionLog().size());
		assertTrue(sessione.getInteractionLog().contains("log1"));
		assertTrue(sessione.getInteractionLog().contains("log2"));
	}
	
	@Test
	void testLogsImmutabili() {
		Developer dev = new Developer(1, "Dev", "dev@test.com", "pass");
		Modello modello = new Modello(1, "TestModel", "Desc", 5.0, "s.bin", "c.json", new ModelProvider(2, "p", "e", "p"));
		Sessione sessione = new Sessione(1, dev, modello, new GPU(1));
		
		assertThrows(UnsupportedOperationException.class, 
				() -> sessione.getInteractionLog().add("log"));
	}

	@Test
	void testTotalTokensTracking() {
		Developer dev = new Developer(1, "Dev", "dev@test.com", "pass");
		Modello modello = new Modello(1, "TestModel", "Desc", 5.0, "s.bin", "c.json", new ModelProvider(2, "p", "e", "p"));
		Sessione sessione = new Sessione(1, dev, modello, new GPU(1));

		sessione.addTokens(10);
		assertEquals(10, sessione.getTokensUsed());
		assertEquals(10, sessione.getTotalTokensUsed());

		sessione.addTokens(5);
		assertEquals(15, sessione.getTokensUsed());
		assertEquals(15, sessione.getTotalTokensUsed());
	}

	@Test
	void testTotalTokensWithReset() {
		// Simula il comportamento del BillingService
		Developer dev = new Developer(1, "Dev", "dev@test.com", "pass");
		Modello modello = new Modello(1, "TestModel", "Desc", 5.0, "s.bin", "c.json", new ModelProvider(2, "p", "e", "p"));
		Sessione sessione = new Sessione(1, dev, modello, new GPU(1));

		sessione.addTokens(10);
		// BillingService resetta i token fatturati
		sessione.setTokensUsed(0);

		assertEquals(0, sessione.getTokensUsed());
		assertEquals(10, sessione.getTotalTokensUsed());

		sessione.addTokens(5);
		assertEquals(5, sessione.getTokensUsed());
		assertEquals(15, sessione.getTotalTokensUsed());
	}

	@Test
	void testTotalCost() {
		Developer dev = new Developer(1, "Dev", "dev@test.com", "pass");
		Modello modello = new Modello(1, "TestModel", "Desc", 5.0, "s.bin", "c.json", new ModelProvider(2, "p", "e", "p"));
		Sessione sessione = new Sessione(1, dev, modello, new GPU(1));

		sessione.addTotalCost(0.5);
		assertEquals(0.5, sessione.getTotalCost());

		sessione.addTotalCost(0.25);
		assertEquals(0.75, sessione.getTotalCost());
	}
}
