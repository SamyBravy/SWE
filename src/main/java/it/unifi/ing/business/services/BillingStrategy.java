package it.unifi.ing.business.services;

import it.unifi.ing.domain.Session;

/**
 * Strategy interface for billing calculation.
 * UML: BillingStrategy
 */
public interface BillingStrategy {
	double calculateCost(Session session);
}
