package it.unifi.ing.controllers;

import it.unifi.ing.business.services.ComplaintService;
import it.unifi.ing.business.services.SessionService;
import it.unifi.ing.domain.Complaint;
import it.unifi.ing.domain.ComplaintStatus;
import it.unifi.ing.domain.Developer;
import it.unifi.ing.domain.Session;
import it.unifi.ing.domain.Transaction;

import java.util.List;

/**
 * Controller responsible for displaying user statistics and reports.
 */
public class StatsController {

	private final SessionService sessionService;
	private final ComplaintService complaintService;

	public StatsController(SessionService sessionService, ComplaintService complaintService) {
		this.sessionService = sessionService;
		this.complaintService = complaintService;
	}

	public void showDeveloperStats(Developer developer) {
		System.out.println("\n╔══════════════════════════════════════╗");
		System.out.println("║   DEVELOPER STATS                    ║");
		System.out.println("╚══════════════════════════════════════╝");
		System.out.println("  Name: " + developer.getName());
		System.out.println("  Email: " + developer.getEmail());
		System.out.println("  Balance: €" + String.format("%.2f", developer.getWallet().getBalance()));

		List<Session> sessions = sessionService.findByUser(developer.getId());
		int totalSessions = sessions.size();
		int activeSessions = (int) sessions.stream().filter(Session::isActive).count();
		int totalTokens = sessions.stream().mapToInt(Session::getTotalTokensUsed).sum();

		System.out.println("\n Session Stats:");
		System.out.println("  - Total sessions: " + totalSessions);
		System.out.println("  - Active sessions: " + activeSessions);
		System.out.println("  - Total tokens used: " + totalTokens);

		if (!sessions.isEmpty()) {
			System.out.println("\n  Session History:");
			for (Session s : sessions) {
				System.out.println("    " + s);
			}
		}

		List<Complaint> devComplaints = complaintService.findByDeveloper(developer.getId());

		if (!devComplaints.isEmpty()) {
			System.out.println("\n  Complaints:");
			for (Complaint c : devComplaints) {
				System.out.println("    #" + c.getId() + " | Model: " + c.getModel().getName()
						+ " | Status: " + c.getStatus());
				if (c.getStatus() == ComplaintStatus.REJECTED && c.getRejectionReasons() != null) {
					System.out.println("      Rejection reason: " + c.getRejectionReasons());
				}
			}
		}

		List<Transaction> transactions = developer.getWallet().getTransactionHistory();
		if (!transactions.isEmpty()) {
			System.out.println("\n  Transaction History:");
			for (Transaction t : transactions) {
				System.out.println("    " + t);
			}
		}
	}
}
