package it.unifi.ing.controllers;

import it.unifi.ing.business.services.ComplaintService;
import it.unifi.ing.business.services.ModelService;
import it.unifi.ing.business.services.SessionService;
import it.unifi.ing.domain.AiModel;
import it.unifi.ing.domain.Complaint;
import it.unifi.ing.domain.ComplaintStatus;
import it.unifi.ing.domain.Developer;
import it.unifi.ing.domain.Supervisor;

import java.util.List;
import java.util.Scanner;

/**
 * Controller for complaint operations: filing (Developer) and review (Supervisor).
 */
public class ComplaintController {

	private final ComplaintService complaintService;
	private final ModelService modelService;
	private final SessionService sessionService;
	private final Scanner scanner;

	public ComplaintController(ComplaintService complaintService, ModelService modelService,
			SessionService sessionService, Scanner scanner) {
		this.complaintService = complaintService;
		this.modelService = modelService;
		this.sessionService = sessionService;
		this.scanner = scanner;
	}

	// ===== DEVELOPER SIDE =====

	public void fileComplaint(Developer developer) {
		List<AiModel> models = modelService.getApprovedModels();
		if (models.isEmpty()) {
			System.out.println("No models available for complaint.");
			return;
		}

		System.out.println("\n--- FILE COMPLAINT ---");
		for (AiModel m : models) {
			System.out.println("  ID: " + m.getId() + " | " + m.getName());
		}

		System.out.print("Model ID: ");
		int id;
		try {
			id = Integer.parseInt(scanner.nextLine().trim());
		} catch (NumberFormatException e) {
			System.out.println("Invalid ID.");
			return;
		}

		AiModel model = modelService.findById(id);
		if (model == null) {
			System.out.println("Model not found.");
			return;
		}

		System.out.print("Describe the issue: ");
		String description = scanner.nextLine().trim();

		List<String> promptLogs = sessionService.getRecentLogs(developer, model);
		if (!promptLogs.isEmpty()) {
			System.out.println("Logs from last session attached (" + promptLogs.size() + " interactions).");
		}

		try {
			complaintService.fileComplaint(developer, model, description, promptLogs);
			System.out.println("Complaint filed. Pending review.");
		} catch (IllegalArgumentException e) {
			System.out.println("Error filing complaint: " + e.getMessage());
		}
	}

	// ===== SUPERVISOR SIDE =====

	public void showReviewDashboard(Supervisor supervisor) {
		while (true) {
			System.out.println("\n╔══════════════════════════════════════╗");
			System.out.println("║   COMPLAINTS DASHBOARD               ║");
			System.out.println("╠══════════════════════════════════════╣");
			System.out.println("║  1. Pending complaints               ║");
			System.out.println("║  2. All complaints                   ║");
			System.out.println("║  0. Back                             ║");
			System.out.println("╚══════════════════════════════════════╝");
			System.out.print("Choice: ");

			String choice = scanner.nextLine().trim();

			switch (choice) {
				case "1" -> handlePendingComplaints();
				case "2" -> viewAllComplaints();
				case "0" -> {
					return;
				}
				default -> System.out.println("Invalid choice.");
			}
		}
	}

	private void handlePendingComplaints() {
		List<Complaint> pending = complaintService.getPendingComplaints();
		if (pending.isEmpty()) {
			System.out.println("No pending complaints.");
			return;
		}

		System.out.println("\n--- PENDING COMPLAINTS ---");
		for (Complaint c : pending) {
			System.out.println("  ID: " + c.getId() + " | Developer: " + c.getDeveloper().getName()
					+ " | Model: " + c.getModel().getName());
		}

		System.out.print("\nSelect complaint ID to review (0 to go back): ");
		int id;
		try {
			id = Integer.parseInt(scanner.nextLine().trim());
		} catch (NumberFormatException e) {
			System.out.println("Invalid ID.");
			return;
		}
		if (id == 0)
			return;

		Complaint complaint = complaintService.findById(id);
		if (complaint == null || complaint.getStatus() != ComplaintStatus.PENDING_REVIEW) {
			System.out.println("Complaint not found or already reviewed.");
			return;
		}

		reviewComplaint(complaint);
	}

	private void reviewComplaint(Complaint complaint) {
		System.out.println("\n=== REVIEWING COMPLAINT #" + complaint.getId() + " ===");
		System.out.println("  Developer: " + complaint.getDeveloper().getName());
		System.out.println("  Model: " + complaint.getModel().getName());
		System.out.println("  Description: " + complaint.getDescription());

		System.out.println("\n  Prompt Log:");
		for (String log : complaint.getPromptLogs()) {
			System.out.println("    " + log);
		}

		System.out.println("\nDecision:");
		System.out.println("  1. Accept complaint");
		System.out.println("  2. Reject complaint");
		System.out.print("Choice: ");
		String decision = scanner.nextLine().trim();

		if ("1".equals(decision)) {
			System.out.print("Tokens to refund: ");
			int refundedTokens;
			try {
				refundedTokens = Integer.parseInt(scanner.nextLine().trim());
			} catch (NumberFormatException e) {
				System.out.println("Invalid value.");
				return;
			}

			System.out.print("Block model? (y/n): ");
			String blockChoice = scanner.nextLine().trim().toLowerCase();
			boolean blockModel = "y".equals(blockChoice);

			complaintService.acceptComplaint(complaint, refundedTokens, blockModel);
			System.out.println("Complaint #" + complaint.getId() + " accepted."
					+ (refundedTokens > 0 ? " Tokens refunded: " + refundedTokens : ""));

		} else if ("2".equals(decision)) {
			System.out.print("Rejection reason: ");
			String reason = scanner.nextLine().trim();
			complaintService.rejectComplaint(complaint, reason);
			System.out.println("Complaint #" + complaint.getId() + " rejected.");
		}
	}

	private void viewAllComplaints() {
		List<Complaint> all = complaintService.getAllComplaints();
		if (all.isEmpty()) {
			System.out.println("No complaints in the system.");
			return;
		}
		System.out.println("\n--- ALL COMPLAINTS ---");
		for (Complaint c : all) {
			System.out.println("  " + c);
		}
	}
}
