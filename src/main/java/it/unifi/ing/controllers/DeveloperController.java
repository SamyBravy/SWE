package it.unifi.ing.controllers;

import it.unifi.ing.dao.interfaces.ComplaintDao;
import it.unifi.ing.dao.interfaces.SessionDao;
import it.unifi.ing.domain.Complaint;
import it.unifi.ing.domain.ComplaintStatus;
import it.unifi.ing.domain.Developer;
import it.unifi.ing.domain.Session;
import it.unifi.ing.domain.Transaction;

import java.util.List;
import java.util.Scanner;

public class DeveloperController {

	private final SessionDao sessionDao;
	private final ComplaintDao complaintDao;
	private final SessionController sessionController;
	private final ComplaintManagementController complaintController;
	private final Scanner scanner;

	public DeveloperController(SessionDao sessionDao, ComplaintDao complaintDao,
			SessionController sessionController,
			ComplaintManagementController complaintController,
			Scanner scanner) {
		this.sessionDao = sessionDao;
		this.complaintDao = complaintDao;
		this.sessionController = sessionController;
		this.complaintController = complaintController;
		this.scanner = scanner;
	}

	public void showMenu(Developer developer) {
		while (true) {
			System.out.println("\n╔══════════════════════════════════════╗");
			System.out.println("║   DEVELOPER MENU                     ║");
			System.out.println("╠══════════════════════════════════════╣");
			System.out.println("║  1. Start session (AI Chat)          ║");
			System.out.println("║  2. Top-up credit                    ║");
			System.out.println("║  3. Stats                            ║");
			System.out.println("║  4. File complaint                   ║");
			System.out.println("║  0. Logout                           ║");
			System.out.println("╚══════════════════════════════════════╝");
			System.out.println("  Balance: €" + String.format("%.2f", developer.getWallet().getBalance()));
			System.out.print("Choice: ");

			String choice = scanner.nextLine().trim();

			switch (choice) {
				case "1" -> sessionController.startSession(developer);
				case "2" -> topUpCredit(developer);
				case "3" -> viewStats(developer);
				case "4" -> complaintController.fileComplaintMenu(developer);
				case "0" -> {
					return;
				}
				default -> System.out.println("Invalid choice.");
			}
		}
	}

	public void topUpCredit(Developer developer) {
		System.out.println("\n--- TOP-UP CREDIT ---");
		System.out.println("Current balance: €" + String.format("%.2f", developer.getWallet().getBalance()));
		System.out.print("Amount to add (€): ");

		try {
			double amount = Double.parseDouble(scanner.nextLine().trim());
			if (amount <= 0) {
				System.out.println("Amount must be positive.");
				return;
			}
			developer.getWallet().addFunds(amount);
			System.out.println("Top-up of €" + String.format("%.2f", amount) + " successful!");
			System.out.println("   New balance: €" + String.format("%.2f", developer.getWallet().getBalance()));
		} catch (NumberFormatException e) {
			System.out.println("Invalid amount.");
		}
	}

	public void viewStats(Developer developer) {
		System.out.println("\n╔══════════════════════════════════════╗");
		System.out.println("║   DEVELOPER STATS                    ║");
		System.out.println("╚══════════════════════════════════════╝");
		System.out.println("  Name: " + developer.getName());
		System.out.println("  Email: " + developer.getEmail());
		System.out.println("  Balance: €" + String.format("%.2f", developer.getWallet().getBalance()));

		List<Session> sessions = sessionDao.findByUser(developer.getId());
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

		List<Complaint> allComplaints = complaintDao.findAll();
		List<Complaint> devComplaints = allComplaints.stream()
				.filter(c -> c.getDeveloper().getId() == developer.getId())
				.toList();

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
