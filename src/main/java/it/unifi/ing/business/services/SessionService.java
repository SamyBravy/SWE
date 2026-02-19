package it.unifi.ing.business.services;

import it.unifi.ing.dao.interfaces.SessioneDAO;
import it.unifi.ing.domain.ClusterGPU;
import it.unifi.ing.domain.Developer;
import it.unifi.ing.domain.GPU;
import it.unifi.ing.domain.Modello;
import it.unifi.ing.domain.Sessione;
import it.unifi.ing.domain.StatoGPU;

/**
 * Service per la gestione delle sessioni di chat con modelli AI.
 */
public class SessionService {

	private final SessioneDAO sessioneDao;
	private final ClusterGPU cluster;
	private final BillingService billingService;
	private int nextId;

	public SessionService(SessioneDAO sessioneDao, ClusterGPU cluster, BillingService billingService) {
		this.sessioneDao = sessioneDao;
		this.cluster = cluster;
		this.billingService = billingService;
		this.nextId = 1;
	}

	/**
	 * Apre una nuova sessione per un Developer con un modello approvato.
	 * 
	 * @return la sessione creata, oppure null se non ci sono GPU disponibili
	 */
	public Sessione openSession(Developer developer, Modello model) {
		GPU gpu = cluster.assegnaGpu();
		if (gpu == null) {
			return null; // nessuna GPU disponibile
		}

		gpu.setModelloCaricato(model);
		Sessione sessione = new Sessione(nextId++, developer, model, gpu);
		sessioneDao.save(sessione);
		return sessione;
	}

	/**
	 * Invia un prompt al modello durante una sessione attiva.
	 * Simula una risposta del modello e incrementa i token usati.
	 * 
	 * @return la risposta del modello
	 */
	public String sendPrompt(Sessione session, String prompt) {
		if (!session.isAttiva()) {
			return "Errore: la sessione è già terminata.";
		}

		// Verifica che la GPU sia ancora operativa
		if (session.getGpu().getStato() == StatoGPU.SURRISCALDATA) {
			return "Errore: la GPU assegnata è surriscaldata. La sessione verrà terminata.";
		}

		// Calcola i token che verranno consumati
		int tokensConsumed = (int) Math.ceil(prompt.length() / 2);

		// Verifica se il developer ha credito sufficiente per questo prompt
		double costoPrompt = tokensConsumed * session.getModello().getCostoTotalePerToken();
		double saldoDisponibile = session.getUtente().getWallet().getSaldo();
		if (costoPrompt > saldoDisponibile) {
			// Credito insufficiente: chiudi automaticamente la sessione
			closeSession(session);
			return "❌ Credito insufficiente per continuare (servono €" + String.format("%.2f", costoPrompt)
					+ ", disponibili €" + String.format("%.2f", saldoDisponibile)
					+ "). Sessione terminata automaticamente.";
		}

// Consuma i token e addebita immediatamente (real-time billing)
		boolean addebitato = session.getUtente().getWallet().deduciCredito(costoPrompt);
		if (!addebitato) {
			// Questo caso è già coperto dal controllo preliminare, ma per sicurezza:
			return "❌ Errore durante l'addebito. Sessione terminata.";
		}
		
		session.addTokens(tokensConsumed);
		session.addTotalCost(costoPrompt); // Aggiorna statistiche costi
		sessioneDao.update(session);

		// Simula risposta del modello
		String response = "[" + session.getModello().getNome() + "] Risposta al prompt: \"" + prompt
				+ "\" — (token usati: " + tokensConsumed + ", costo: €" + String.format("%.4f", costoPrompt)
				+ ", saldo: €" + String.format("%.2f", session.getUtente().getWallet().getSaldo()) + ")";

		// Logga l'interazione
		session.addLog("[" + java.time.LocalDateTime.now() + "] Prompt: " + prompt + " | Response: " + response);

		return response;
	}

	/**
	 * Chiude una sessione, calcola e addebita il costo al developer.
	 * 
	 * @return il costo addebitato, oppure -1 se fondi insufficienti
	 */
	public double closeSession(Sessione session) {
		if (!session.isAttiva()) {
			return 0;
		}

		// Il costo è già stato addebitato prompt per prompt, quindi qui
		// calcoliamo solo il totale per mostrarlo all'utente
		double costoTotale = billingService.calcolaCosto(session);

		// Chiudi la sessione e rilascia la GPU
		session.chiudi();
		sessioneDao.update(session);
		cluster.rilasciaGpu(session.getGpu());

		return costoTotale;
	}

	/**
	 * Trova una sessione per ID.
	 */
	public Sessione findById(int id) {
		return sessioneDao.findById(id);
	}

	/**
	 * Restituisce i log dell'ultima sessione (attiva o chiusa) tra developer e modello.
	 */
	public java.util.List<String> getRecentLogs(Developer developer, Modello modello) {
		// Cerca l'ultima sessione creata (ID più alto)
		return sessioneDao.findAll().stream()
				.filter(s -> s.getUtente().getId() == developer.getId() && s.getModello().getId() == modello.getId())
				.max((s1, s2) -> Integer.compare(s1.getId(), s2.getId()))
				.map(Sessione::getInteractionLog)
				.orElse(java.util.Collections.emptyList());
	}
}
