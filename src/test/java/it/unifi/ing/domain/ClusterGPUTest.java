package it.unifi.ing.domain;

import it.unifi.ing.dao.interfaces.GpuDAO;
import it.unifi.ing.dao.memory.InMemoryGpuDAO;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

import static org.junit.jupiter.api.Assertions.*;

class ClusterGPUTest {

	private ClusterGPU cluster;
	private GpuDAO gpuDao;

	@BeforeEach
	void setUp() {
		ClusterGPU.resetInstance();
		cluster = ClusterGPU.getInstance();
		gpuDao = new InMemoryGpuDAO();
		gpuDao.save(new GPU(1));
		gpuDao.save(new GPU(2));
		gpuDao.save(new GPU(3));
		cluster.init(gpuDao);
	}

	@AfterEach
	void tearDown() {
		ClusterGPU.resetInstance();
	}

	@Test
	void testSingletonInstance() {
		ClusterGPU instance1 = ClusterGPU.getInstance();
		ClusterGPU instance2 = ClusterGPU.getInstance();
		assertSame(instance1, instance2);
	}

	@Test
	void testAssegnaGpu() {
		GPU gpu = cluster.assegnaGpu();
		assertNotNull(gpu);
		assertEquals(StatoGPU.OCCUPATA, gpu.getStato());
	}

	@Test
	void testAssegnaTutteLeGpu() {
		assertNotNull(cluster.assegnaGpu());
		assertNotNull(cluster.assegnaGpu());
		assertNotNull(cluster.assegnaGpu());
		// Tutte occupate
		assertNull(cluster.assegnaGpu());
	}

	@Test
	void testRilasciaGpu() {
		GPU gpu = cluster.assegnaGpu();
		assertNotNull(gpu);
		cluster.rilasciaGpu(gpu);
		assertTrue(gpu.isLibera());
		assertNull(gpu.getModelloCaricato());
	}

	@Test
	void testGetGpuDisponibili() {
		assertEquals(3, cluster.getGpuDisponibili().size());
		cluster.assegnaGpu();
		assertEquals(2, cluster.getGpuDisponibili().size());
	}

	@Test
	void testGetTutteLeGpu() {
		assertEquals(3, cluster.getTutteLeGpu().size());
	}

	@Test
	void testResetInstance() {
		ClusterGPU first = ClusterGPU.getInstance();
		ClusterGPU.resetInstance();
		ClusterGPU second = ClusterGPU.getInstance();
		assertNotSame(first, second);
	}
}
