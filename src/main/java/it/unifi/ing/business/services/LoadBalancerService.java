package it.unifi.ing.business.services;

import it.unifi.ing.dao.interfaces.SessioneDAO;
import it.unifi.ing.domain.ClusterGPU;
import it.unifi.ing.domain.GPU;
import it.unifi.ing.domain.GpuObserver;
import it.unifi.ing.domain.Sessione;
import it.unifi.ing.domain.StatoGPU;

import java.util.List;

/**
 * LoadBalancerService: Observer per il monitoraggio delle GPU.
 * Alla ricezione di un allarme di surriscaldamento, interviene
 * spegnendo la GPU e terminando la sessione per sicurezza.
 * Gestisce anche il bilanciamento del carico tra le GPU (Use Case 5).
 */
public class LoadBalancerService implements GpuObserver {

	private final SessioneDAO sessioneDao;
	private final ClusterGPU cluster;
	private final BillingService billingService;

	public LoadBalancerService(SessioneDAO sessioneDao, ClusterGPU cluster, BillingService billingService) {
		this.sessioneDao = sessioneDao;
		this.cluster = cluster;
		this.billingService = billingService;
	}

	@Override
	public void onTemperatureAlert(GPU gpu) {
		System.out.println("\n⚠️  ALLARME: GPU " + gpu.getId()
				+ " ha superato i 90°C! Temperatura: " + String.format("%.1f", gpu.getTemperatura()) + "°C");

		// Cerca la sessione attiva su questa GPU
		List<Sessione> tutteLeSessioni = sessioneDao.findAll();
		for (Sessione sessione : tutteLeSessioni) {
			if (sessione.isAttiva() && sessione.getGpu().getId() == gpu.getId()) {
				System.out.println("🔴 Terminazione forzata della sessione " + sessione.getId()
						+ " per sicurezza (utente: " + sessione.getUtente().getNome() + ")");

				// Addebita il costo prima di chiudere
				billingService.addebitaCosto(sessione);

				// Chiudi la sessione
				sessione.chiudi();
				sessioneDao.update(sessione);

				break;
			}
		}

		// Rilascia la GPU
		cluster.rilasciaGpu(gpu);
		System.out.println("✅ GPU " + gpu.getId() + " rilasciata e pronta per il raffreddamento.");
	}

	/**
	 * Registra questo observer su tutte le GPU del cluster.
	 */
	public void registraSuTutteLeGpu() {
		for (GPU gpu : cluster.getTutteLeGpu()) {
			gpu.addObserver(this);
		}
	}

	/**
	 * Bilancia il carico tra le GPU (Use Case 5 - Bilanciamento del Carico).
	 * Distribuisce il carico uniformemente tra le GPU attive con un margine del 10%.
	 * Attiva GPU in IDLE se il carico supera l'80%.
	 */
	public void balanceLoad() {
		List<GPU> tutteLeGpu = cluster.getTutteLeGpu();
		if (tutteLeGpu.isEmpty()) {
			return;
		}

		// Conta GPU occupate e calcola carico totale
		List<GPU> gpuAttive = tutteLeGpu.stream()
				.filter(g -> g.getStato() == StatoGPU.OCCUPATA)
				.toList();

		if (gpuAttive.size() < 2) {
			return; // Pre-condizione: almeno 2 GPU in uso
		}

		double caricoTotale = gpuAttive.stream()
				.mapToDouble(GPU::getLoadPercentage)
				.sum();

		// Calcola distribuzione ottimale
		double caricoPrevisto = caricoTotale / gpuAttive.size();

		// Se il carico previsto supera l'80%, attiva GPU libere per alleviare
		if (caricoPrevisto > 80.0) {
			List<GPU> gpuLibere = tutteLeGpu.stream()
					.filter(g -> g.getStato() == StatoGPU.LIBERA)
					.toList();

			// Attiva GPU finché il carico non scende sotto il 70%
			for (GPU gpuLibera : gpuLibere) {
				gpuLibera.setStato(StatoGPU.OCCUPATA);
				gpuAttive = tutteLeGpu.stream()
						.filter(g -> g.getStato() == StatoGPU.OCCUPATA)
						.toList();
				caricoPrevisto = caricoTotale / gpuAttive.size();

				System.out.println("⚡ GPU " + gpuLibera.getId()
						+ " attivata per bilanciamento carico.");

				if (caricoPrevisto <= 70.0) {
					break;
				}
			}

			// Ricalcola dopo eventuali attivazioni
			if (caricoPrevisto > 80.0) {
				// Impossibile soddisfare il vincolo: distribuzione uniforme senza vincolo
				System.out.println("⚠️  Carenza di GPU: il carico sarà distribuito uniformemente"
						+ " ignorando il vincolo del carico massimo.");
			}
		}

		// Distribuisci il carico uniformemente tra le GPU attive
		List<GPU> gpuFinali = tutteLeGpu.stream()
				.filter(g -> g.getStato() == StatoGPU.OCCUPATA)
				.toList();

		if (!gpuFinali.isEmpty()) {
			double caricoUniforme = caricoTotale / gpuFinali.size();
			for (GPU gpu : gpuFinali) {
				gpu.setLoadPercentage(caricoUniforme);
			}
			System.out.println("⚖️  Carico bilanciato: " + String.format("%.1f", caricoUniforme)
					+ "% su " + gpuFinali.size() + " GPU.");
		}
	}
}
