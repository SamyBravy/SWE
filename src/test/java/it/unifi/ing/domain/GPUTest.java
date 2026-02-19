package it.unifi.ing.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

class GPUTest {

	private GPU gpu;
	private boolean alertReceived;
	private GPU alertedGpu;

	@BeforeEach
	void setUp() {
		gpu = new GPU(1);
		alertReceived = false;
		alertedGpu = null;
	}

	@Test
	void testGpuCreazione() {
		assertEquals(1, gpu.getId());
		assertEquals(30.0, gpu.getTemperatura());
		assertEquals(StatoGPU.LIBERA, gpu.getStato());
		assertNull(gpu.getModelloCaricato());
		assertTrue(gpu.isLibera());
	}

	@Test
	void testSetTemperaturaNormale() {
		gpu.setTemperatura(50.0);
		assertEquals(50.0, gpu.getTemperatura());
		assertNotEquals(StatoGPU.SURRISCALDATA, gpu.getStato());
	}

	@Test
	void testSetTemperaturaAllarme() {
		gpu.addObserver(g -> {
			alertReceived = true;
			alertedGpu = g;
		});
		gpu.setTemperatura(95.0);
		assertTrue(alertReceived);
		assertEquals(gpu, alertedGpu);
		assertEquals(StatoGPU.SURRISCALDATA, gpu.getStato());
	}

	@Test
	void testObserverNonNotificatoSottoSoglia() {
		gpu.addObserver(g -> alertReceived = true);
		gpu.setTemperatura(89.0);
		assertFalse(alertReceived);
	}

	@Test
	void testRemoveObserver() {
		GpuObserver observer = g -> alertReceived = true;
		gpu.addObserver(observer);
		gpu.removeObserver(observer);
		gpu.setTemperatura(95.0);
		assertFalse(alertReceived);
	}

	@Test
	void testMultipleObservers() {
		final int[] count = { 0 };
		gpu.addObserver(g -> count[0]++);
		gpu.addObserver(g -> count[0]++);
		gpu.setTemperatura(95.0);
		assertEquals(2, count[0]);
	}

	@Test
	void testSimulaTickOccupata() {
		gpu.setStato(StatoGPU.OCCUPATA);
		double tempIniziale = gpu.getTemperatura();
		gpu.simulaTick();
		assertTrue(gpu.getTemperatura() > tempIniziale);
	}

	@Test
	void testSimulaTickLibera() {
		gpu.setStato(StatoGPU.LIBERA);
		// Imposta una temperatura alta senza triggerare l'observer
		gpu.setStato(StatoGPU.LIBERA);
		// direttamente modifica la temperatura per testare il cooling
		gpu.setTemperatura(50.0); // sotto soglia
		gpu.setStato(StatoGPU.LIBERA); // reset stato
		gpu.simulaTick();
		assertTrue(gpu.getTemperatura() < 50.0);
	}

	@Test
	void testIsLibera() {
		assertTrue(gpu.isLibera());
		gpu.setStato(StatoGPU.OCCUPATA);
		assertFalse(gpu.isLibera());
	}

	@Test
	void testNoDuplicateObservers() {
		final int[] count = { 0 };
		GpuObserver obs = g -> count[0]++;
		gpu.addObserver(obs);
		gpu.addObserver(obs); // aggiunta duplicata
		gpu.setTemperatura(95.0);
		assertEquals(1, count[0]);
	}
}
