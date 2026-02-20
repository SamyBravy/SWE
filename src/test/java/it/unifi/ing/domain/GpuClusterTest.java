package it.unifi.ing.domain;

import it.unifi.ing.dao.memory.InMemoryGpuDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

class GpuClusterTest {

	private GpuCluster cluster;
	private InMemoryGpuDao gpuDao;

	@BeforeEach
	void setUp() {
		GpuCluster.resetInstance();
		gpuDao = new InMemoryGpuDao();
		gpuDao.save(new GPU(1));
		gpuDao.save(new GPU(2));
		gpuDao.save(new GPU(3));
		cluster = GpuCluster.getInstance();
		cluster.init(gpuDao);
	}

	@AfterEach
	void tearDown() {
		GpuCluster.resetInstance();
	}

	@Test
	void testSingleton() {
		assertSame(cluster, GpuCluster.getInstance());
	}

	@Test
	void testGetAvailableGpu() {
		GPU gpu = cluster.getAvailableGpu();
		assertNotNull(gpu);
		assertEquals(GpuStatus.INACTIVE, gpu.getStatus());
	}

	@Test
	void testReleaseGpu() {
		GPU gpu = cluster.getAvailableGpu();
		cluster.releaseGpu(gpu);
		assertEquals(GpuStatus.ACTIVE, gpu.getStatus());
		assertNull(gpu.getLoadedModel());
	}

	@Test
	void testNoAvailableGpu() {
		cluster.getAvailableGpu();
		cluster.getAvailableGpu();
		cluster.getAvailableGpu();
		assertNull(cluster.getAvailableGpu());
	}

	@Test
	void testGetAllGpus() {
		assertEquals(3, cluster.getAllGpus().size());
	}

	@Test
	void testGetAvailableGpus() {
		cluster.getAvailableGpu();
		assertEquals(2, cluster.getAvailableGpus().size());
	}

	@Test
	void testGetAvailableGpuSetsModel() {
		GPU gpu = cluster.getAvailableGpu();
		assertNull(gpu.getLoadedModel());
	}
}
