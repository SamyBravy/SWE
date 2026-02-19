package it.unifi.ing.business.services;

import it.unifi.ing.dao.memory.InMemoryModelloDAO;
import it.unifi.ing.dao.memory.InMemoryGpuDAO;
import it.unifi.ing.domain.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class VerificationServiceTest {

	private VerificationService verificationService;
	private InMemoryModelloDAO modelloDao;
	private ClusterGPU cluster;
	private ModelProvider provider;

	@BeforeEach
	void setUp() {
		ClusterGPU.resetInstance();
		modelloDao = new InMemoryModelloDAO();
		InMemoryGpuDAO gpuDao = new InMemoryGpuDAO();
		gpuDao.save(new GPU(1));
		gpuDao.save(new GPU(2));

		cluster = ClusterGPU.getInstance();
		cluster.init(gpuDao);

		verificationService = new VerificationService(modelloDao, cluster);
		provider = new ModelProvider(1, "Provider1", "prov@test.com", "pass");
	}

	@AfterEach
	void tearDown() {
		ClusterGPU.resetInstance();
	}

	@Test
	void testLoadOnGpu() {
		Modello modello = new Modello(1, "TestModel", "Desc", 5.0, "s.bin", "c.json", provider);
		modelloDao.save(modello);

		GPU gpu = verificationService.loadOnGpu(modello);
		assertNotNull(gpu);
		assertEquals(modello, gpu.getModelloCaricato());
		assertEquals(StatoGPU.OCCUPATA, gpu.getStato());
	}

	@Test
	void testLoadOnGpuNoDisponibili() {
		Modello modello = new Modello(1, "TestModel", "Desc", 5.0, "s.bin", "c.json", provider);
		// Occupa tutte le GPU
		cluster.assegnaGpu();
		cluster.assegnaGpu();

		GPU gpu = verificationService.loadOnGpu(modello);
		assertNull(gpu);
	}

	@Test
	void testRunBenchmarks() {
		Modello modello = new Modello(1, "TestModel", "Desc", 5.0, "s.bin", "c.json", provider);
		GPU gpu = new GPU(1);

		Map<String, Object> results = verificationService.runBenchmarks(modello, gpu);
		assertNotNull(results);
		assertTrue(results.containsKey("latenza_media_ms"));
		assertTrue(results.containsKey("throughput_tokens_sec"));
		assertTrue(results.containsKey("modello"));
	}

	@Test
	void testRunEthicsTest() {
		Modello modello = new Modello(1, "TestModel", "Desc", 5.0, "s.bin", "c.json", provider);

		String result = verificationService.runEthicsTest(modello, "Test prompt");
		assertNotNull(result);
		assertTrue(result.contains("TestModel"));
	}

	@Test
	void testApproveModel() {
		Modello modello = new Modello(1, "TestModel", "Desc", 5.0, "s.bin", "c.json", provider);
		modelloDao.save(modello);

		verificationService.approveModel(modello, 0.01);
		assertEquals(StatoModello.APPROVATO, modello.getStato());
		assertEquals(0.01, modello.getCostoPerTokenPiattaforma());
	}

	@Test
	void testRejectModel() {
		Modello modello = new Modello(1, "TestModel", "Desc", 5.0, "s.bin", "c.json", provider);
		modelloDao.save(modello);

		verificationService.rejectModel(modello, "Qualità insufficiente");
		assertEquals(StatoModello.RIFIUTATO, modello.getStato());
	}

	@Test
	void testReleaseGpu() {
		Modello modello = new Modello(1, "TestModel", "Desc", 5.0, "s.bin", "c.json", provider);
		GPU gpu = verificationService.loadOnGpu(modello);
		assertNotNull(gpu);

		verificationService.releaseGpu(gpu);
		assertTrue(gpu.isLibera());
	}
}
