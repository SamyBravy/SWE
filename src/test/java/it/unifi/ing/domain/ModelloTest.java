package it.unifi.ing.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ModelloTest {

	@Test
	void testCreazioneModello() {
		ModelProvider provider = new ModelProvider(1, "Provider1", "prov@email.com", "pass");
		Modello modello = new Modello(1, "GPT-Test", "Modello di test", 0.05,
				"/path/safetensors", "/path/config.json", provider);

		assertEquals(1, modello.getId());
		assertEquals("GPT-Test", modello.getNome());
		assertEquals("Modello di test", modello.getDescrizione());
		assertEquals(0.05, modello.getCostoPerTokenProvider());
		assertEquals("/path/safetensors", modello.getSafetensorsPath());
		assertEquals("/path/config.json", modello.getJsonPath());
		assertEquals(StatoModello.IN_ATTESA, modello.getStato());
		assertEquals(provider, modello.getProvider());
		assertEquals(0.0, modello.getCostoPerTokenPiattaforma());
		assertEquals(0.05, modello.getCostoTotalePerToken());
	}

	@Test
	void testCambioStato() {
		ModelProvider provider = new ModelProvider(1, "Provider1", "prov@email.com", "pass");
		Modello modello = new Modello(1, "TestModel", "Desc", 0.05, "s.bin", "c.json", provider);

		assertEquals(StatoModello.IN_ATTESA, modello.getStato());

		modello.setStato(StatoModello.APPROVATO);
		assertEquals(StatoModello.APPROVATO, modello.getStato());

		modello.setStato(StatoModello.RIFIUTATO);
		assertEquals(StatoModello.RIFIUTATO, modello.getStato());
	}

	@Test
	void testCostoTotalePerToken() {
		ModelProvider provider = new ModelProvider(1, "Provider1", "prov@email.com", "pass");
		Modello modello = new Modello(1, "TestModel", "Desc", 0.05, "s.bin", "c.json", provider);

		// Solo costo provider
		assertEquals(0.05, modello.getCostoTotalePerToken());

		// Aggiungi costo piattaforma
		modello.setCostoPerTokenPiattaforma(0.03);
		assertEquals(0.08, modello.getCostoTotalePerToken(), 0.0001);

		// Il totale è la somma
		assertEquals(0.05, modello.getCostoPerTokenProvider());
		assertEquals(0.03, modello.getCostoPerTokenPiattaforma());
	}
}
