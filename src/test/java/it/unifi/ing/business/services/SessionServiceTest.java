package it.unifi.ing.business.services;

import it.unifi.ing.dao.memory.InMemoryGpuDao;
import it.unifi.ing.dao.memory.InMemorySessionDao;
import it.unifi.ing.domain.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

class SessionServiceTest {

	private SessionService sessionService;
	private InMemorySessionDao sessionDao;
	private GpuCluster cluster;
	private Developer developer;
	private AiModel model;

	@BeforeEach
	void setUp() {
		GpuCluster.resetInstance();
		sessionDao = new InMemorySessionDao();
		InMemoryGpuDao gpuDao = new InMemoryGpuDao();
		gpuDao.save(new GPU(1));
		gpuDao.save(new GPU(2));

		cluster = GpuCluster.getInstance();
		cluster.init(gpuDao);

		sessionService = new SessionService(sessionDao, cluster);

		developer = new Developer(1, "Dev", "dev@test.com", "pass");
		developer.getWallet().addFunds(100.0);

		ModelProvider prov = new ModelProvider(2, "Prov", "prov@test.com", "pass");
		model = AiModel.submitForReview(1, "TestModel", "Desc", 0.005, "s.safetensors", "c.json", prov);
		model.setCostPerTokenPlatform(0.005);
	}

	@AfterEach
	void tearDown() {
		GpuCluster.resetInstance();
	}

	@Test
	void testSuccessfulSession() {
		Session session = sessionService.openSession(developer, model);
		assertNotNull(session);
		assertNotNull(sessionService.findById(session.getId()));
		assertTrue(session.isActive());
		assertFalse(session.getGpus().isEmpty());

		double balanceBefore = developer.getWallet().getBalance();
		String response = sessionService.sendPrompt(session, "Hello AI");

		assertNotNull(response);
		assertTrue(developer.getWallet().getBalance() < balanceBefore);

		java.util.List<String> logs = sessionService.getRecentLogs(developer, model);
		assertEquals(2, logs.size());

		sessionService.closeSession(session);
		assertFalse(session.isActive());
		for (GPU g : session.getGpus()) {
			assertEquals(GpuStatus.INACTIVE, g.getStatus());
		}
	}

	@Test
	void testSessionErrors() {
		Session s1 = sessionService.openSession(developer, model);
		Session s2 = sessionService.openSession(developer, model);
		Session failedSession = sessionService.openSession(developer, model);
		assertNull(failedSession);

		sessionService.closeSession(s1);
		sessionService.closeSession(s2);

		Developer poorDev = new Developer(3, "Poor", "poor@test.com", "pass");
		poorDev.getWallet().addFunds(0.001);
		Session poorSession = sessionService.openSession(poorDev, model);

		String errorResponse = sessionService.sendPrompt(poorSession, "A very long prompt that costs a lot");
		assertTrue(errorResponse.contains("Insufficient credit") || errorResponse.contains("Error"));
		assertFalse(poorSession.isActive());

		String closedResponse = sessionService.sendPrompt(poorSession, "Should fail");
		assertTrue(closedResponse.contains("Error"));
	}
}
