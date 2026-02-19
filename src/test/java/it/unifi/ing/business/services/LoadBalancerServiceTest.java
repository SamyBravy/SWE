package it.unifi.ing.business.services;

import it.unifi.ing.dao.memory.InMemoryGpuDAO;
import it.unifi.ing.dao.memory.InMemorySessioneDAO;
import it.unifi.ing.domain.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

import static org.junit.jupiter.api.Assertions.*;

class LoadBalancerServiceTest {

	private LoadBalancerService loadBalancerService;
	private InMemorySessioneDAO sessioneDao;
	private ClusterGPU cluster;
	private GPU gpu;
	private Developer developer;
	private Modello modello;

	@BeforeEach
	void setUp() {
		ClusterGPU.resetInstance();
		sessioneDao = new InMemorySessioneDAO();
		InMemoryGpuDAO gpuDao = new InMemoryGpuDAO();
		gpu = new GPU(1);
		gpuDao.save(gpu);

		cluster = ClusterGPU.getInstance();
		cluster.init(gpuDao);

		BillingService billingService = new BillingService(new StandardBillingStrategy());
		loadBalancerService = new LoadBalancerService(sessioneDao, cluster, billingService);

		developer = new Developer(1, "Dev", "dev@test.com", "pass");
		developer.getWallet().addCredito(100.0);

		ModelProvider provider = new ModelProvider(2, "Prov", "prov@test.com", "pass");
		modello = new Modello(1, "TestModel", "Desc", 0.005, "s.bin", "c.json", provider);
		modello.setCostoPerTokenPiattaforma(0.005);
		// getCostoTotalePerToken() = 0.01
	}

	@AfterEach
	void tearDown() {
		ClusterGPU.resetInstance();
	}

	@Test
	void testRegistraSuTutteLeGpu() {
		loadBalancerService.registraSuTutteLeGpu();
		// Triggera allarme — se l'observer è registrato, il codice deve eseguire senza
		// errori
		gpu.setStato(StatoGPU.OCCUPATA);
		// Nessuna sessione attiva su questa GPU, quindi l'observer non dovrebbe
		// crashare
		gpu.setTemperatura(95.0);
		assertEquals(StatoGPU.LIBERA, gpu.getStato()); // GPU rilasciata dal LoadBalancer
	}

	@Test
	void testOnTemperatureAlertConSessioneAttiva() {
		// Crea una sessione attiva sulla GPU
		gpu.setStato(StatoGPU.OCCUPATA);
		Sessione sessione = new Sessione(1, developer, modello, gpu);
		sessione.addTokens(100);
		sessioneDao.save(sessione);

		// Simula l'allarme
		loadBalancerService.onTemperatureAlert(gpu);

		// La sessione deve essere chiusa
		assertFalse(sessione.isAttiva());
		// La GPU deve essere rilasciata
		assertEquals(StatoGPU.LIBERA, gpu.getStato());
	}

	@Test
	void testOnTemperatureAlertSenzaSessione() {
		gpu.setStato(StatoGPU.OCCUPATA);
		// Nessuna sessione — non deve crashare
		loadBalancerService.onTemperatureAlert(gpu);
		assertEquals(StatoGPU.LIBERA, gpu.getStato());
	}

	@Test
	void testOnTemperatureAlertAddebitaCosto() {
		gpu.setStato(StatoGPU.OCCUPATA);
		Sessione sessione = new Sessione(1, developer, modello, gpu);
		sessione.addTokens(500);
		sessioneDao.save(sessione);

		double saldoPrima = developer.getWallet().getSaldo();
		loadBalancerService.onTemperatureAlert(gpu);

		// Il costo deve essere stato addebitato: 500 * 0.01 = 5.0
		assertEquals(saldoPrima - 5.0, developer.getWallet().getSaldo(), 0.001);
	}
}
