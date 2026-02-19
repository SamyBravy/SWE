package it.unifi.ing.business.services;

import it.unifi.ing.dao.interfaces.SessioneDAO;
import it.unifi.ing.domain.Sessione;

import java.util.List;

/**
 * Service per il calcolo e l'addebito dei costi di sessione.
 * Utilizza il pattern Strategy per selezionare la logica di billing.
 */
public class BillingService {

	private BillingStrategy billingStrategy;
	private SessioneDAO sessioneDao;

	public BillingService(BillingStrategy billingStrategy) {
		this.billingStrategy = billingStrategy;
	}

	public BillingService(BillingStrategy billingStrategy, SessioneDAO sessioneDao) {
		this.billingStrategy = billingStrategy;
		this.sessioneDao = sessioneDao;
	}

	/**
	 * Cambia la strategia di billing a runtime.
	 */
	public void setBillingStrategy(BillingStrategy billingStrategy) {
		this.billingStrategy = billingStrategy;
	}

	public BillingStrategy getBillingStrategy() {
		return billingStrategy;
	}

	/**
	 * Calcola il costo di una sessione.
	 */
	public double calcolaCosto(Sessione sessione) {
		return billingStrategy.calculateCost(sessione);
	}

	/**
	 * Addebita il costo della sessione al wallet del developer.
	 * 
	 * @return true se l'addebito è avvenuto con successo, false se fondi
	 *         insufficienti
	 */
	public boolean addebitaCosto(Sessione sessione) {
		double costo = calcolaCosto(sessione);
		if (costo <= 0) {
			return true; // nulla da addebitare
		}
		sessione.addTotalCost(costo);
		return sessione.getUtente().getWallet().deduciCredito(costo);
	}

	/**
	 * Addebita i costi per tutte le sessioni attive (Use Case 6 - Addebito Token).
	 * Per ogni sessione attiva, calcola il costo dei token non ancora addebitati,
	 * scala dal wallet e azzera il contatore.
	 * Se il wallet va in negativo, chiude la sessione.
	 */
	public void billActiveSessions() {
		if (sessioneDao == null) {
			return;
		}

		List<Sessione> sessioniAttive = sessioneDao.findActiveSessions();
		for (Sessione sessione : sessioniAttive) {
			if (sessione.getTokensUsed() <= 0) {
				continue;
			}

			double costo = calcolaCosto(sessione);
			boolean addebitato = sessione.getUtente().getWallet().deduciCredito(costo);
			sessione.addTotalCost(costo);

			// Azzera il contatore dei token non addebitati
			sessione.setTokensUsed(0);
			sessioneDao.update(sessione);

			if (!addebitato) {
				// Credito esaurito: chiudi la sessione
				System.out.println("⚠️  Credito esaurito per " + sessione.getUtente().getNome()
						+ ". Sessione " + sessione.getId() + " chiusa automaticamente.");
				sessione.chiudi();
				sessioneDao.update(sessione);
			}
		}
	}
}
