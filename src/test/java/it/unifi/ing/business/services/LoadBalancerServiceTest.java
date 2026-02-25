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
	private InMemoryGpuDao gpuDao;
	private GpuCluster cluster;
	private GPU gpu;
	private Developer developer;
	private AiModel model;

	@BeforeEach
	void setUp() {
		GpuCluster.resetInstance();
		sessionDao = new InMemorySessionDao();
		gpuDao = new InMemoryGpuDao();
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
	void testTemperatureAlerts() {
		loadBalancerService.registerOnAllGpus();

		gpu.setStatus(GpuStatus.ACTIVE);
		gpu.setTemperature(95.0);

		assertEquals(GpuStatus.INACTIVE, gpu.getStatus());

		gpu.setStatus(GpuStatus.ACTIVE);
		gpu.setTemperature(30.0);
		Session session = new Session(1, developer, model, gpu);
		session.addUsedTokens(100);
		sessionDao.save(session);

		gpu.setTemperature(95.0);

		assertFalse(session.isActive());
		assertEquals(GpuStatus.INACTIVE, gpu.getStatus());
	}

	@Test
	void testLoadBalancing() {
		gpu.setStatus(GpuStatus.ACTIVE);
		gpu.setLoadPercentage(90.0);
		Session session = new Session(1, developer, model, gpu);
		sessionDao.save(session);

		GPU gpu2 = new GPU(2);
		gpuDao.save(gpu2);

		loadBalancerService.balanceLoad();

		assertEquals(2, session.getGpus().size());
		assertEquals(GpuStatus.ACTIVE, gpu2.getStatus());
		assertEquals(45.0, gpu.getLoadPercentage());
		assertEquals(45.0, gpu2.getLoadPercentage());

		gpu.setLoadPercentage(15.0);
		gpu2.setLoadPercentage(15.0);

		loadBalancerService.balanceLoad();

		assertEquals(1, session.getGpus().size());
		assertEquals(GpuStatus.INACTIVE, gpu2.getStatus());
		assertEquals(30.0, gpu.getLoadPercentage());

		gpu.setLoadPercentage(60.0);
		GPU gpu3 = new GPU(3);
		gpu3.setStatus(GpuStatus.ACTIVE);
		gpu3.setLoadPercentage(40.0);
		session.addGpu(gpu3);
		gpuDao.save(gpu3);

		loadBalancerService.balanceLoad();

		assertEquals(2, session.getGpus().size());
		assertEquals(50.0, gpu.getLoadPercentage());
		assertEquals(50.0, gpu3.getLoadPercentage());
	}

	@Test
	void testLoadBalancingGpuShortage() {
		gpu.setStatus(GpuStatus.ACTIVE);
		gpu.setLoadPercentage(90.0);
		Session session = new Session(1, developer, model, gpu);
		sessionDao.save(session);

		loadBalancerService.balanceLoad();

		assertEquals(1, session.getGpus().size());
		assertEquals(90.0, gpu.getLoadPercentage());
	}

}
