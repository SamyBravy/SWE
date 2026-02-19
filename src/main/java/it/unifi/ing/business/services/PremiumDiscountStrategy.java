package it.unifi.ing.business.services;

import it.unifi.ing.domain.Sessione;

/**
 * Strategia di billing premium: applica uno sconto percentuale al costo
 * standard.
 */
public class PremiumDiscountStrategy implements BillingStrategy {

	private final double discountPercentage;

	/**
	 * @param discountPercentage percentuale di sconto (es. 0.20 per 20%)
	 */
	public PremiumDiscountStrategy(double discountPercentage) {
		if (discountPercentage < 0 || discountPercentage > 1) {
			throw new IllegalArgumentException("Lo sconto deve essere tra 0 e 1");
		}
		this.discountPercentage = discountPercentage;
	}

	@Override
	public double calculateCost(Sessione session) {
		double standardCost = session.getTokensUsed() * session.getModello().getCostoTotalePerToken();
		return standardCost * (1.0 - discountPercentage);
	}

	public double getDiscountPercentage() {
		return discountPercentage;
	}
}
