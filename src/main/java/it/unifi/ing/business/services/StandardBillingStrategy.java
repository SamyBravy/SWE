package it.unifi.ing.business.services;

import it.unifi.ing.domain.Session;

/**
 * Standard billing strategy: cost = unbilledTokens * costPerToken.
 */
public class StandardBillingStrategy implements BillingStrategy {

	@Override
	public double calculateCost(Session session) {
		return session.getUnbilledUsedTokens() * session.getModel().getCostPerToken();
	}
}
