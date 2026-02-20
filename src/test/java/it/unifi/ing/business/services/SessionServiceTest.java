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

		BillingService billingService = new BillingService(new StandardBillingStrategy());
		sessionService = new SessionService(sessionDao, cluster, billingService);

		developer = new Developer(1, "Dev", "dev@test.com", "pass");
		developer.getWallet().addFunds(100.0);

		ModelProvider prov = new ModelProvider(2, "Prov", "prov@test.com", "pass");
		model = AiModel.submitForReview(1, "TestModel", "Desc", 0.005, "s.bin", "c.json", prov);
		model.setCostPerTokenPlatform(0.005);
	}

	@AfterEach
	void tearDown() {
		GpuCluster.resetInstance();
	}

	@Test
	void testOpenSession() {
		Session session = sessionService.openSession(developer, model);
		assertNotNull(session);
		assertTrue(session.isActive());
		assertNotNull(session.getGpu());
	}

	@Test
	void testOpenSessionNoGpu() {
		sessionService.openSession(developer, model);
		sessionService.openSession(developer, model);
		assertNull(sessionService.openSession(developer, model));
	}

	@Test
	void testSendPrompt() {
		Session session = sessionService.openSession(developer, model);
		String response = sessionService.sendPrompt(session, "Hello AI");
		assertNotNull(response);
		assertTrue(response.contains("Hello AI"));
	}

	@Test
	void testSendPromptInsufficientCredit() {
		Developer poorDev = new Developer(3, "Poor", "poor@test.com", "pass");
		poorDev.getWallet().addFunds(0.001);
		Session session = sessionService.openSession(poorDev, model);
		String response = sessionService.sendPrompt(session, "This should fail because tokens will cost more");
		assertTrue(response.contains("Insufficient credit") || response.contains("Error"));
	}

	@Test
	void testCloseSession() {
		Session session = sessionService.openSession(developer, model);
		sessionService.closeSession(session);
		assertFalse(session.isActive());
		assertEquals(GpuStatus.ACTIVE, session.getGpu().getStatus());
	}

	@Test
	void testSendPromptClosedSession() {
		Session session = sessionService.openSession(developer, model);
		sessionService.closeSession(session);
		String response = sessionService.sendPrompt(session, "Should fail");
		assertTrue(response.contains("Error"));
	}

	@Test
	void testSendPromptOverheatedGpu() {
		Session session = sessionService.openSession(developer, model);
		session.getGpu().setStatus(GpuStatus.IDLE);
		String response = sessionService.sendPrompt(session, "Should fail");
		assertTrue(response.contains("overheated"));
	}

	@Test
	void testFindById() {
		Session session = sessionService.openSession(developer, model);
		assertNotNull(sessionService.findById(session.getId()));
	}

	@Test
	void testSendPromptSingleCharacter() {
		Session session = sessionService.openSession(developer, model);
		String response = sessionService.sendPrompt(session, "d");
		assertNotNull(response);
		assertFalse(response.contains("Error"));
	}

	@Test
	void testGetRecentLogs() {
		Session session = sessionService.openSession(developer, model);
		sessionService.sendPrompt(session, "Hello");
		java.util.List<String> logs = sessionService.getRecentLogs(developer, model);
		assertEquals(2, logs.size());
	}

	@Test
	void testTokensDeductedFromBalance() {
		Session session = sessionService.openSession(developer, model);
		double balanceBefore = developer.getWallet().getBalance();
		sessionService.sendPrompt(session, "Hello");
		assertTrue(developer.getWallet().getBalance() < balanceBefore);
	}
}
