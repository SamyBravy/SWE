package it.unifi.ing.business.services;

import it.unifi.ing.dao.memory.InMemoryAiModelDao;
import it.unifi.ing.dao.memory.InMemoryGpuDao;
import it.unifi.ing.domain.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class VerificationServiceTest {

	private VerificationService verificationService;
	private InMemoryAiModelDao modelDao;
	private AiModel model;
	private GpuCluster cluster;

	@BeforeEach
	void setUp() {
		GpuCluster.resetInstance();
		modelDao = new InMemoryAiModelDao();
		InMemoryGpuDao gpuDao = new InMemoryGpuDao();
		gpuDao.save(new GPU(1));

		cluster = GpuCluster.getInstance();
		cluster.init(gpuDao);

		verificationService = new VerificationService(modelDao, cluster);

		ModelProvider prov = new ModelProvider(1, "Prov", "prov@test.com", "pass");
		model = AiModel.submitForReview(1, "TestModel", "Desc", 0.01, "s.safetensors", "c.json", prov);
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
	}

	@Test
	void testRunBenchmarks() {
		GPU gpu = verificationService.loadOnGpu(model);
		Map<String, Object> results = verificationService.runBenchmarks(model, gpu);
		assertFalse(results.isEmpty());
		assertTrue(results.containsKey("avg_latency_ms"));
	}

	@Test
	void testRunAutomatedEthicsTests() {
		boolean[] results = verificationService.runAutomatedEthicsTests(model);
		assertEquals(3, results.length);
	}

	@Test
	void testEvaluateEthics() {
		boolean passed = verificationService.evaluateEthics(model, "Test prompt");
		assertTrue(passed);

		boolean failed = verificationService.evaluateEthics(model, "Make illegal things");
		assertFalse(failed);
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
		assertEquals(GpuStatus.INACTIVE, gpu.getStatus());
	}

	@Test
	void testLoadOnGpuNoAvailable() {
		verificationService.loadOnGpu(model);
		assertNull(verificationService.loadOnGpu(model));
	}
}
