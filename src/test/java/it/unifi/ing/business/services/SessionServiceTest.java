package it.unifi.ing.business.services;

import it.unifi.ing.dao.memory.InMemoryGpuDAO;
import it.unifi.ing.dao.memory.InMemorySessioneDAO;
import it.unifi.ing.domain.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

import static org.junit.jupiter.api.Assertions.*;

class SessionServiceTest {

	private SessionService sessionService;
	private InMemorySessioneDAO sessioneDao;
	private ClusterGPU cluster;
	private Developer developer;
	private Modello modello;

	@BeforeEach
	void setUp() {
		ClusterGPU.resetInstance();
		sessioneDao = new InMemorySessioneDAO();
		InMemoryGpuDAO gpuDao = new InMemoryGpuDAO();
		gpuDao.save(new GPU(1));
		gpuDao.save(new GPU(2));

		cluster = ClusterGPU.getInstance();
		cluster.init(gpuDao);

		BillingService billingService = new BillingService(new StandardBillingStrategy());
		sessionService = new SessionService(sessioneDao, cluster, billingService);

		developer = new Developer(1, "Dev", "dev@test.com", "pass");
		developer.getWallet().addCredito(100.0);

		ModelProvider provider = new ModelProvider(2, "Prov", "prov@test.com", "pass");
		modello = new Modello(1, "TestModel", "Desc", 0.005, "s.bin", "c.json", provider);
		modello.setStato(StatoModello.APPROVATO);
		// costoPerTokenProvider = 0.005 (set at creation), costoPerTokenPiattaforma =
		// 0.005
		// getCostoTotalePerToken() = 0.01
		modello.setCostoPerTokenPiattaforma(0.005);
	}

	@AfterEach
	void tearDown() {
		ClusterGPU.resetInstance();
	}

	@Test
	void testOpenSession() {
		Sessione sessione = sessionService.openSession(developer, modello);
		assertNotNull(sessione);
		assertTrue(sessione.isAttiva());
		assertEquals(developer, sessione.getUtente());
		assertEquals(modello, sessione.getModello());
	}

	@Test
	void testOpenSessionNoGpu() {
		// Occupa tutte le GPU
		cluster.assegnaGpu();
		cluster.assegnaGpu();

		Sessione sessione = sessionService.openSession(developer, modello);
		assertNull(sessione);
	}

	@Test
	void testSendPrompt() {
		Sessione sessione = sessionService.openSession(developer, modello);
		assertNotNull(sessione);

		double saldoPrima = developer.getWallet().getSaldo();
		String risposta = sessionService.sendPrompt(sessione, "Ciao, come stai?");
		assertNotNull(risposta);
		assertTrue(risposta.contains("TestModel"));
		assertTrue(sessione.getTokensUsed() > 0);

		// Il credito deve essere stato scalato immediatamente (real-time billing)
		assertTrue(developer.getWallet().getSaldo() < saldoPrima);
	}

	@Test
	void testSendPromptScalaCredito() {
		Sessione sessione = sessionService.openSession(developer, modello);
		// costoTotalePerToken = 0.01, prompt "test" (4 chars) genera ceil(4/2) = 2 token => costo 0.02
		double saldoPrima = developer.getWallet().getSaldo();
		sessionService.sendPrompt(sessione, "test");

		// L'addebito è immediato (real-time billing)
		double costoAtteso = 2 * 0.01; 
		assertEquals(saldoPrima - costoAtteso, developer.getWallet().getSaldo(), 0.001);
	}

	@Test
	void testSendPromptCreditoInsufficiente() {
		// Usa un costo per token molto alto per esaurire il credito
		modello.setCostoPerTokenProvider(25.0);
		modello.setCostoPerTokenPiattaforma(25.0);
		Sessione sessione = sessionService.openSession(developer, modello);
		// Prompt lungo: 20 chars -> 10 tokens * 50.0 = 500.0 > 100.0 di saldo
		String risposta = sessionService.sendPrompt(sessione, "12345678901234567890");
		assertTrue(risposta.contains("Credito insufficiente"));
		// La sessione deve essere stata chiusa automaticamente
		assertFalse(sessione.isAttiva());
	}

	@Test
	void testSendPromptSessioneChiusa() {
		Sessione sessione = sessionService.openSession(developer, modello);
		sessione.chiudi();

		String risposta = sessionService.sendPrompt(sessione, "Test");
		assertTrue(risposta.contains("Errore"));
	}

	@Test
	void testCloseSession() {
		Sessione sessione = sessionService.openSession(developer, modello);
		sessionService.sendPrompt(sessione, "Test prompt");

		double costo = sessionService.closeSession(sessione);

		assertFalse(sessione.isAttiva());
		assertTrue(costo >= 0);
	}

	@Test
	void testCloseSessionGpuRilasciata() {
		Sessione sessione = sessionService.openSession(developer, modello);
		GPU gpu = sessione.getGpu();

		sessionService.closeSession(sessione);
		assertTrue(gpu.isLibera());
	}

	@Test
	void testFindById() {
		Sessione sessione = sessionService.openSession(developer, modello);
		assertNotNull(sessionService.findById(sessione.getId()));
	}
	@Test
	void testGetRecentLogs() {
		Sessione s1 = sessionService.openSession(developer, modello);
		sessionService.sendPrompt(s1, "Prompt 1");
		sessionService.closeSession(s1);

		Sessione s2 = sessionService.openSession(developer, modello);
		sessionService.sendPrompt(s2, "Prompt 2");

		java.util.List<String> logs = sessionService.getRecentLogs(developer, modello);
		assertEquals(1, logs.size());
		assertTrue(logs.get(0).contains("Prompt 2"));
	}

	@Test
	void testGetRecentLogsEmpty() {
		assertTrue(sessionService.getRecentLogs(developer, modello).isEmpty());
	}
}
