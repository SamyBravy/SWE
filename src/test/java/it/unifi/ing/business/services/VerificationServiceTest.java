package it.unifi.ing.business.services;

import it.unifi.ing.dao.memory.InMemoryAiModelDAO;
import it.unifi.ing.dao.memory.InMemoryGpuDAO;
import it.unifi.ing.domain.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class VerificationServiceTest {

	private VerificationService verificationService;
	private InMemoryAiModelDAO modelDao;
	private AiModel model;
	private GpuCluster cluster;

	@BeforeEach
	void setUp() {
		GpuCluster.resetInstance();
		modelDao = new InMemoryAiModelDAO();
		InMemoryGpuDAO gpuDao = new InMemoryGpuDAO();
		gpuDao.save(new GPU(1));

		cluster = GpuCluster.getInstance();
		cluster.init(gpuDao);

		verificationService = new VerificationService(modelDao, cluster);

		ModelProvider prov = new ModelProvider(1, "Prov", "prov@test.com", "pass");
		model = new AiModel(1, "TestModel", "Desc", 0.01, "s.bin", "c.json", prov);
		modelDao.save(model);
	}

	@AfterEach
	void tearDown() {
		GpuCluster.resetInstance();
	}

	@Test
	void testLoadOnGpu() {
		GPU gpu = verificationService.loadOnGpu(model);
		assertNotNull(gpu);
		assertEquals(model, gpu.getLoadedModel());
	}

	@Test
	void testRunBenchmarks() {
		GPU gpu = verificationService.loadOnGpu(model);
		Map<String, Object> results = verificationService.runBenchmarks(model, gpu);
		assertFalse(results.isEmpty());
		assertTrue(results.containsKey("avg_latency_ms"));
	}

	@Test
	void testRunEthicsTest() {
		String response = verificationService.runEthicsTest(model, "Test prompt");
		assertNotNull(response);
		assertTrue(response.contains("TestModel"));
	}

	@Test
	void testApproveModel() {
		verificationService.approveModel(model, 0.005);
		assertEquals(ModelStatus.APPROVED, model.getStatus());
		assertEquals(0.005, model.getCostPerTokenPlatform());
	}

	@Test
	void testRejectModel() {
		verificationService.rejectModel(model, "Too slow");
		assertEquals(ModelStatus.REJECTED, model.getStatus());
	}

	@Test
	void testReleaseGpu() {
		GPU gpu = verificationService.loadOnGpu(model);
		verificationService.releaseGpu(gpu);
		assertEquals(GpuStatus.ACTIVE, gpu.getStatus());
	}

	@Test
	void testLoadOnGpuNoAvailable() {
		verificationService.loadOnGpu(model);
		assertNull(verificationService.loadOnGpu(model));
	}
}
