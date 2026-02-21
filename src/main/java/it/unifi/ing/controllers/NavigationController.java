package it.unifi.ing.controllers;

import it.unifi.ing.domain.Developer;
import it.unifi.ing.domain.ModelProvider;
import it.unifi.ing.domain.Supervisor;
import it.unifi.ing.domain.User;

import java.util.Scanner;

/**
 * Central navigation controller: handles menu routing for all user roles.
 */
public class NavigationController {

	private final AuthController authController;
	private final SessionController sessionController;
	private final ModelController modelController;
	private final VerificationController verificationController;
	private final ComplaintController complaintController;
	private final WalletController walletController;
	private final StatsController statsController;
	private final Scanner scanner;

	public NavigationController(AuthController authController,
			SessionController sessionController,
			ModelController modelController,
			VerificationController verificationController,
			ComplaintController complaintController,
			WalletController walletController,
			StatsController statsController,
			Scanner scanner) {
		this.authController = authController;
		this.sessionController = sessionController;
		this.modelController = modelController;
		this.verificationController = verificationController;
		this.complaintController = complaintController;
		this.walletController = walletController;
		this.statsController = statsController;
		this.scanner = scanner;
	}

	public void start() {
		while (true) {
			User user = authController.showMenu();

			if (user instanceof Developer dev) {
				showDeveloperMenu(dev);
			} else if (user instanceof ModelProvider provider) {
				showModelProviderMenu(provider);
			} else if (user instanceof Supervisor supervisor) {
				showSupervisorMenu(supervisor);
			}
		}
	}

	private void showDeveloperMenu(Developer developer) {
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
				case "2" -> walletController.topUpCredit(developer);
				case "3" -> statsController.showDeveloperStats(developer);
				case "4" -> complaintController.fileComplaint(developer);
				case "0" -> {
					return;
				}
				default -> System.out.println("Invalid choice.");
			}
		}
	}

	private void showModelProviderMenu(ModelProvider provider) {
		while (true) {
			System.out.println("\n╔══════════════════════════════════════╗");
			System.out.println("║   MODEL PROVIDER MENU                ║");
			System.out.println("╠══════════════════════════════════════╣");
			System.out.println("║  1. Publish Model                    ║");
			System.out.println("║  2. My Models                        ║");
			System.out.println("║  0. Logout                           ║");
			System.out.println("╚══════════════════════════════════════╝");
			System.out.print("Choice: ");

			String choice = scanner.nextLine().trim();

			switch (choice) {
				case "1" -> modelController.publishModel(provider);
				case "2" -> modelController.viewModels();
				case "0" -> {
					return;
				}
				default -> System.out.println("Invalid choice.");
			}
		}
	}

	private void showSupervisorMenu(Supervisor supervisor) {
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
				case "2" -> complaintController.showReviewDashboard(supervisor);
				case "0" -> {
					return;
				}
				default -> System.out.println("Invalid choice.");
			}
		}
	}

}
