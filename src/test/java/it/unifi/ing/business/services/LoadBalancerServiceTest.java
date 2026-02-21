package it.unifi.ing.business.services;

import it.unifi.ing.dao.memory.InMemoryGpuDao;
import it.unifi.ing.dao.memory.InMemorySessionDao;
import it.unifi.ing.domain.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

class LoadBalancerServiceTest {

	private LoadBalancerService loadBalancerService;
	private InMemorySessionDao sessionDao;
	private GpuCluster cluster;
	private GPU gpu;
	private Developer developer;
	private AiModel model;

	@BeforeEach
	void setUp() {
		GpuCluster.resetInstance();
		sessionDao = new InMemorySessionDao();
		InMemoryGpuDao gpuDao = new InMemoryGpuDao();
		gpu = new GPU(1);
		gpuDao.save(gpu);

		cluster = GpuCluster.getInstance();
		cluster.init(gpuDao);

		SessionService sessionService = new SessionService(sessionDao, cluster);
		loadBalancerService = new LoadBalancerService(sessionService, cluster, new EvenLoadBalancingStrategy());

		developer = new Developer(1, "Dev", "dev@test.com", "pass");
		developer.getWallet().addFunds(100.0);

		ModelProvider provider = new ModelProvider(2, "Prov", "prov@test.com", "pass");
		model = AiModel.submitForReview(1, "TestModel", "Desc", 0.005, "s.safetensors", "c.json", provider);
		model.setCostPerTokenPlatform(0.005);
	}

	@AfterEach
	void tearDown() {
		GpuCluster.resetInstance();
	}

	@Test
	void testRegisterOnAllGpus() {
		loadBalancerService.registerOnAllGpus();
		gpu.setStatus(GpuStatus.ACTIVE);
		gpu.setTemperature(95.0);
		assertEquals(GpuStatus.INACTIVE, gpu.getStatus());
	}

	@Test
	void testOnTemperatureAlertWithActiveSession() {
		gpu.setStatus(GpuStatus.ACTIVE);
		Session session = new Session(1, developer, model, gpu);
		session.addUsedTokens(100);
		sessionDao.save(session);

		loadBalancerService.update(gpu, "TEMPERATURE_ALERT");

		assertFalse(session.isActive());
		assertEquals(GpuStatus.INACTIVE, gpu.getStatus());
	}

	@Test
	void testOnTemperatureAlertWithoutSession() {
		gpu.setStatus(GpuStatus.ACTIVE);
		loadBalancerService.update(gpu, "TEMPERATURE_ALERT");
		assertEquals(GpuStatus.INACTIVE, gpu.getStatus());
	}

}
