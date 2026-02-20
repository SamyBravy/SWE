package it.unifi.ing.business.services;

import it.unifi.ing.dao.interfaces.SessionDao;
import it.unifi.ing.domain.Session;

import java.util.List;

/**
 * Service for billing calculation and cost charging.
 * Uses the Strategy pattern for billing logic.
 */
public class BillingService {

	private BillingStrategy billingStrategy;
	private SessionDao sessionDao;

	public BillingService(BillingStrategy billingStrategy) {
		this.billingStrategy = billingStrategy;
	}

	public BillingService(BillingStrategy billingStrategy, SessionDao sessionDao) {
		this.billingStrategy = billingStrategy;
		this.sessionDao = sessionDao;
	}

	public void setBillingStrategy(BillingStrategy billingStrategy) {
		this.billingStrategy = billingStrategy;
	}

	public BillingStrategy getBillingStrategy() {
		return billingStrategy;
	}

	public double calculateCost(Session session) {
		return billingStrategy.calculateCost(session);
	}

	/**
	 * Charges the session cost to the developer's wallet.
	 */
	public boolean chargeCost(Session session) {
		double cost = calculateCost(session);
		if (cost <= 0) {
			return true;
		}
		session.addTotalCost(cost);
		return session.getDeveloper().getWallet().charge(cost);
	}

	/**
	 * Bills all active sessions (periodic billing).
	 */
	public void billActiveSessions() {
		if (sessionDao == null) {
			return;
		}

		List<Session> activeSessions = sessionDao.findActiveSessions();
		for (Session session : activeSessions) {
			if (session.getUnbilledUsedTokens() <= 0) {
				continue;
			}

			double cost = calculateCost(session);
			boolean charged = session.getDeveloper().getWallet().charge(cost);
			session.addTotalCost(cost);

			session.resetTokens();
			sessionDao.update(session);

			if (!charged) {
				System.out.println("⚠️  Credit exhausted for " + session.getDeveloper().getName()
						+ ". Session " + session.getId() + " closed automatically.");
				session.close();
				sessionDao.update(session);
			}
		}
	}
}
