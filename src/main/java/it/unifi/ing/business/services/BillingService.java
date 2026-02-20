package it.unifi.ing.business.services;

import it.unifi.ing.domain.Session;

/**
 * Service for billing calculation and cost charging.
 * Uses the Strategy pattern for billing logic.
 */
public class BillingService {

	private BillingStrategy billingStrategy;

	public BillingService(BillingStrategy billingStrategy) {
		this.billingStrategy = billingStrategy;
	}

	public void setBillingStrategy(BillingStrategy billingStrategy) {
		this.billingStrategy = billingStrategy;
	}

	public BillingStrategy getBillingStrategy() {
		return billingStrategy;
	}

	public double calculateCost(Session session, int tokensConsumed) {
		return billingStrategy.calculateCost(session, tokensConsumed);
	}
}
