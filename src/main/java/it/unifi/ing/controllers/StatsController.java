package it.unifi.ing.controllers;

import it.unifi.ing.business.services.StatsService;
import it.unifi.ing.domain.Complaint;
import it.unifi.ing.domain.ComplaintStatus;
import it.unifi.ing.domain.Developer;
import it.unifi.ing.domain.Session;
import it.unifi.ing.domain.Transaction;

/**
 * Controller responsible for displaying user statistics and reports.
 */
public class StatsController {

	private final StatsService statsService;

	public StatsController(StatsService statsService) {
		this.statsService = statsService;
	}

	public void showDeveloperStats(Developer developer) {
		StatsService.DeveloperStats stats = statsService.getDeveloperStats(developer);

		System.out.println("\n╔══════════════════════════════════════╗");
		System.out.println("║   DEVELOPER STATS                    ║");
		System.out.println("╚══════════════════════════════════════╝");
		System.out.println("  Name: " + developer.getName());
		System.out.println("  Email: " + developer.getEmail());
		System.out.println("  Balance: €" + String.format("%.2f", developer.getWallet().getBalance()));

		System.out.println("\n Session Stats:");
		System.out.println("  - Total sessions: " + stats.totalSessions());
		System.out.println("  - Active sessions: " + stats.activeSessions());
		System.out.println("  - Total tokens used: " + stats.totalTokens());

		if (!stats.sessions().isEmpty()) {
			System.out.println("\n  Session History:");
			for (Session s : stats.sessions()) {
				System.out.println("    " + s);
			}
		}

		if (!stats.complaints().isEmpty()) {
			System.out.println("\n  Complaints:");
			for (Complaint c : stats.complaints()) {
				System.out.println("    #" + c.getId() + " | Model: " + c.getModel().getName()
						+ " | Status: " + c.getStatus());
				if (c.getStatus() == ComplaintStatus.REJECTED && c.getRejectionReasons() != null) {
					System.out.println("      Rejection reason: " + c.getRejectionReasons());
				}
			}
		}

		if (!stats.transactions().isEmpty()) {
			System.out.println("\n  Transaction History:");
			for (Transaction t : stats.transactions()) {
				System.out.println("    " + t);
			}
		}
	}
}
