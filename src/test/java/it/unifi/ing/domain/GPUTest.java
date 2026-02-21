package it.unifi.ing.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class GPUTest {

	private GPU gpu;
	private boolean alertReceived;
	private Object alertedSubject;

	@BeforeEach
	void setUp() {
		gpu = new GPU(1);
		alertReceived = false;
		alertedSubject = null;
	}

	@Test
	void testGpuCreation() {
		assertEquals(1, gpu.getId());
		assertEquals(30.0, gpu.getTemperature());
		assertEquals(GpuStatus.INACTIVE, gpu.getStatus());
		assertTrue(gpu.isAvailable());
	}

	@Test
	void testSetTemperatureNormal() {
		gpu.setTemperature(50.0);
		assertEquals(50.0, gpu.getTemperature());
		assertNotEquals(GpuStatus.IDLE, gpu.getStatus());
	}

	@Test
	void testSetTemperatureAlert() {
		gpu.attach((subject, event) -> {
			alertReceived = true;
			alertedSubject = subject;
		});
		gpu.setTemperature(95.0);
		assertTrue(alertReceived);
		assertEquals(gpu, alertedSubject);
		assertEquals(GpuStatus.IDLE, gpu.getStatus());
	}

	@Test
	void testObserverNotNotifiedBelowThreshold() {
		gpu.attach((subject, event) -> alertReceived = true);
		gpu.setTemperature(89.0);
		assertFalse(alertReceived);
	}

	@Test
	void testRemoveObserver() {
		Observer observer = (subject, event) -> alertReceived = true;
		gpu.attach(observer);
		gpu.detach(observer);
		gpu.setTemperature(95.0);
		assertFalse(alertReceived);
	}

	@Test
	void testMultipleObservers() {
		final int[] count = { 0 };
		gpu.attach((subject, event) -> count[0]++);
		gpu.attach((subject, event) -> count[0]++);
		gpu.setTemperature(95.0);
		assertEquals(2, count[0]);
	}

	@Test
	void testSimulateTickInactive() {
		gpu.setStatus(GpuStatus.ACTIVE);
		double initialTemp = gpu.getTemperature();
		gpu.simulateTick();
		assertTrue(gpu.getTemperature() > initialTemp);
	}

	@Test
	void testSimulateTickActive() {
		gpu.setStatus(GpuStatus.INACTIVE);
		gpu.setTemperature(50.0);
		gpu.setStatus(GpuStatus.INACTIVE);
		gpu.simulateTick();
		assertTrue(gpu.getTemperature() < 50.0);
	}

	@Test
	void testIsAvailable() {
		assertTrue(gpu.isAvailable());
		gpu.setStatus(GpuStatus.ACTIVE);
		assertFalse(gpu.isAvailable());
	}

	@Test
	void testNoDuplicateObservers() {
		final int[] count = { 0 };
		Observer obs = (subject, event) -> count[0]++;
		gpu.attach(obs);
		gpu.attach(obs);
		gpu.setTemperature(95.0);
		assertEquals(1, count[0]);
	}
}
