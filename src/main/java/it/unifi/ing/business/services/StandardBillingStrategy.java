package it.unifi.ing.business.services;

import it.unifi.ing.domain.Sessione;

/**
 * Strategia di billing standard: costo = token * costoTotalePerToken del
 * modello.
 */
public class StandardBillingStrategy implements BillingStrategy {

	@Override
	public double calculateCost(Sessione session) {
		return session.getTokensUsed() * session.getModello().getCostoTotalePerToken();
	}
}
