package it.unifi.ing.business.services;

import it.unifi.ing.domain.Sessione;

/**
 * Strategy interface per il calcolo dei costi di una sessione.
 * Pattern Strategy.
 */
public interface BillingStrategy {

	/**
	 * Calcola il costo di una sessione in base ai token utilizzati.
	 * 
	 * @param session la sessione da fatturare
	 * @return il costo calcolato
	 */
	double calculateCost(Sessione session);
}
