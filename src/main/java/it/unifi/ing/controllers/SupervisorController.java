package it.unifi.ing.controllers;

import it.unifi.ing.domain.Supervisor;

import java.util.Scanner;

/**
 * Controller for the Supervisor's main menu.
 */
public class SupervisorController {

	private final VerificationController verificationController;
	private final ComplaintManagementController complaintController;
	private final Scanner scanner;

	public SupervisorController(VerificationController verificationController,
			ComplaintManagementController complaintController,
			Scanner scanner) {
		this.verificationController = verificationController;
		this.complaintController = complaintController;
		this.scanner = scanner;
	}

	public void showMenu(Supervisor supervisor) {
		while (true) {
			System.out.println("\n╔══════════════════════════════════════╗");
			System.out.println("║   SUPERVISOR MENU                    ║");
			System.out.println("╠══════════════════════════════════════╣");
			System.out.println("║  1. Verify models                    ║");
			System.out.println("║  2. Manage complaints                ║");
			System.out.println("║  0. Logout                           ║");
			System.out.println("╚══════════════════════════════════════╝");
			System.out.print("Choice: ");

			String choice = scanner.nextLine().trim();

			switch (choice) {
				case "1" -> verificationController.showMenu(supervisor);
				case "2" -> complaintController.showDashboard(supervisor);
				case "0" -> {
					return;
				}
				default -> System.out.println("Invalid choice.");
			}
		}
	}
}
