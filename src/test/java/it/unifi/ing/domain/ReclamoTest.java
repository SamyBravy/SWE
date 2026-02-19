package it.unifi.ing.domain;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReclamoTest {

	@Test
	void testCreazioneReclamo() {
		ModelProvider provider = new ModelProvider(1, "Prov", "prov@test.com", "pass");
		Developer developer = new Developer(2, "Dev", "dev@test.com", "pass");
		Modello modello = new Modello(1, "Modello1", "Desc", 0.01, "s.bin", "c.json", provider);
		List<String> logs = Arrays.asList("prompt1 -> risposta1", "prompt2 -> risposta2");

		Reclamo reclamo = new Reclamo(1, developer, modello, "Il modello ha dato risposte scorrette", logs);

		assertEquals(1, reclamo.getId());
		assertEquals(developer, reclamo.getDeveloper());
		assertEquals(modello, reclamo.getModello());
		assertEquals(StatoReclamo.IN_ATTESA, reclamo.getStato());
		assertEquals(2, reclamo.getPromptLogs().size());
		assertNull(reclamo.getMotiviRifiuto());
	}

	@Test
	void testCambioStatoReclamo() {
		ModelProvider provider = new ModelProvider(1, "Prov", "prov@test.com", "pass");
		Developer developer = new Developer(2, "Dev", "dev@test.com", "pass");
		Modello modello = new Modello(1, "Modello1", "Desc", 0.01, "s.bin", "c.json", provider);

		Reclamo reclamo = new Reclamo(1, developer, modello, "Test", Arrays.asList("log1"));

		reclamo.setStato(StatoReclamo.ACCETTATO);
		assertEquals(StatoReclamo.ACCETTATO, reclamo.getStato());

		reclamo.setStato(StatoReclamo.RIFIUTATO);
		reclamo.setMotiviRifiuto("Reclamo non fondato");
		assertEquals(StatoReclamo.RIFIUTATO, reclamo.getStato());
		assertEquals("Reclamo non fondato", reclamo.getMotiviRifiuto());
	}

	@Test
	void testPromptLogsImmutabili() {
		ModelProvider provider = new ModelProvider(1, "Prov", "prov@test.com", "pass");
		Developer developer = new Developer(2, "Dev", "dev@test.com", "pass");
		Modello modello = new Modello(1, "Modello1", "Desc", 0.01, "s.bin", "c.json", provider);

		Reclamo reclamo = new Reclamo(1, developer, modello, "Test", Arrays.asList("log1"));

		assertThrows(UnsupportedOperationException.class,
				() -> reclamo.getPromptLogs().add("nuovoLog"));
	}
}
