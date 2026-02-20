package it.unifi.ing.business.services;

import it.unifi.ing.domain.Session;

/**
 * Premium billing strategy: applies a discount to the standard cost.
 */
public class PremiumDiscountStrategy implements BillingStrategy {

	private final double discountPercentage;

	public PremiumDiscountStrategy(double discountPercentage) {
		if (discountPercentage < 0 || discountPercentage > 1) {
			throw new IllegalArgumentException("Discount must be between 0 and 1");
		}
		this.discountPercentage = discountPercentage;
	}

	@Override
	public double calculateCost(Session session, int tokensConsumed) {
		double standardCost = tokensConsumed * session.getModel().getCostPerToken();
		return standardCost * (1.0 - discountPercentage);
	}

	public double getDiscountPercentage() {
		return discountPercentage;
	}
}
