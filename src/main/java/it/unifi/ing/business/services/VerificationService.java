package it.unifi.ing.business.services;

import it.unifi.ing.dao.interfaces.ModelloDAO;
import it.unifi.ing.domain.ClusterGPU;
import it.unifi.ing.domain.GPU;
import it.unifi.ing.domain.Modello;
import it.unifi.ing.domain.StatoModello;

import java.util.HashMap;
import java.util.Map;

/**
 * Service per la verifica dei modelli AI da parte del Supervisor.
 * Supporta: caricamento su GPU, benchmark, test etici, approvazione/rifiuto.
 */
public class VerificationService {

	private final ModelloDAO modelloDao;
	private final ClusterGPU cluster;

	public VerificationService(ModelloDAO modelloDao, ClusterGPU cluster) {
		this.modelloDao = modelloDao;
		this.cluster = cluster;
	}

	/**
	 * Carica un modello su una GPU per la verifica.
	 * 
	 * @return la GPU assegnata, oppure null se non disponibile
	 */
	public GPU loadOnGpu(Modello model) {
		GPU gpu = cluster.assegnaGpu();
		if (gpu != null) {
			gpu.setModelloCaricato(model);
		}
		return gpu;
	}

	/**
	 * Esegue i benchmark del modello sulla GPU assegnata.
	 * 
	 * @return mappa con i risultati dei benchmark (simulati)
	 */
	public Map<String, Object> runBenchmarks(Modello model, GPU gpu) {
		Map<String, Object> results = new HashMap<>();
		// Simulazione benchmark
		results.put("latenza_media_ms", 45 + (int) (Math.random() * 50));
		results.put("throughput_tokens_sec", 100 + (int) (Math.random() * 200));
		results.put("memoria_utilizzata_mb", 1024 + (int) (Math.random() * 2048));
		results.put("accuratezza_percentuale", 85 + Math.random() * 15);
		results.put("modello", model.getNome());
		results.put("gpu_id", gpu.getId());
		return results;
	}

	/**
	 * Esegue un test etico sul modello con un prompt di esempio.
	 * 
	 * @return la risposta simulata del modello
	 */
	public String runEthicsTest(Modello model, String prompt) {
		// Simulazione risposta del modello
		return "[Modello: " + model.getNome() + "] Risposta simulata al prompt: \"" + prompt
				+ "\" — Il modello ha risposto entro i parametri etici stabiliti.";
	}

	/**
	 * Approva il modello, impostando il costo per token della piattaforma.
	 */
	public void approveModel(Modello model, double costoPerTokenPiattaforma) {
		model.setStato(StatoModello.APPROVATO);
		model.setCostoPerTokenPiattaforma(costoPerTokenPiattaforma);
		modelloDao.update(model);
	}

	/**
	 * Rifiuta il modello con le motivazioni fornite.
	 */
	public void rejectModel(Modello model, String reasons) {
		model.setStato(StatoModello.RIFIUTATO);
		modelloDao.update(model);
		System.out.println("Modello '" + model.getNome() + "' rifiutato. Motivo: " + reasons);
	}

	/**
	 * Rilascia la GPU utilizzata per la verifica.
	 */
	public void releaseGpu(GPU gpu) {
		cluster.rilasciaGpu(gpu);
	}
}
