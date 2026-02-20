package it.unifi.ing.view;

import it.unifi.ing.business.Timer;
import it.unifi.ing.business.services.*;
import it.unifi.ing.controllers.*;
import it.unifi.ing.dao.interfaces.*;
import it.unifi.ing.dao.memory.*;
import it.unifi.ing.domain.*;

import java.util.List;
import java.util.Scanner;

public class Main {

	private static SessionService sessionService;
	private static ModelService modelService;
	private static ComplaintService complaintService;
	private static ComplaintDao complaintDao;
	private static Scanner scanner;

	public static void main(String[] args) {
		scanner = new Scanner(System.in);

		// ===== 1. INITIALIZE DAOs =====
		UserDao userDao = new InMemoryUserDao();
		AiModelDao modelDao = new InMemoryAiModelDao();
		SessionDao sessionDao = new InMemorySessionDao();
		GpuDao gpuDao = new InMemoryGpuDao();
		complaintDao = new InMemoryComplaintDao();

		// ===== 2. INITIALIZE GPU CLUSTER =====
		for (int i = 1; i <= 4; i++) {
			gpuDao.save(new GPU(i));
		}

		GpuCluster cluster = GpuCluster.getInstance();
		cluster.init(gpuDao);

		// ===== 3. CREATE SERVICES =====
		AuthService authService = new AuthService(userDao);
		modelService = new ModelService(modelDao);
		BillingStrategy billingStrategy = new StandardBillingStrategy();
		BillingService billingService = new BillingService(billingStrategy);
		sessionService = new SessionService(sessionDao, cluster, billingService);
		VerificationService verificationService = new VerificationService(modelDao, cluster);
		complaintService = new ComplaintService(complaintDao, userDao);

		// LoadBalancerService (Observer) — register on all GPUs
		LoadBalancerService loadBalancer = new LoadBalancerService(sessionDao, cluster, billingService);
		loadBalancer.registerOnAllGpus();

		// ===== 4. START TIMER =====
		Timer timer = Timer.getInstance(cluster);
		timer.configureServices(null, loadBalancer);
		timer.start();

		// ===== 5. CREATE CONTROLLERS =====
		AuthController authController = new AuthController(authService, scanner);
		SessionController sessionController = new SessionController(sessionService, modelService, scanner);
		ModelManagementController modelController = new ModelManagementController(modelService, scanner);
		VerificationController verificationController = new VerificationController(verificationService, modelService,
				scanner);
		ComplaintManagementController complaintController = new ComplaintManagementController(complaintService,
				scanner);
		UserController userController = new UserController(sessionDao, complaintDao, scanner);

		// ===== 6. MAIN LOOP =====
		while (true) {
			User user = authController.showMenu();

			if (user instanceof Developer) {
				developerMenu((Developer) user, sessionController, userController);
			} else if (user instanceof ModelProvider) {
				modelController.showMenu((ModelProvider) user);
			} else if (user instanceof Supervisor) {
				supervisorMenu((Supervisor) user, verificationController, complaintController);
			}
		}
	}

	private static void developerMenu(Developer developer, SessionController sessionController,
			UserController userController) {
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
				case "1":
					sessionController.startSession(developer);
					break;
				case "2":
					userController.topUpCredit(developer);
					break;
				case "3":
					userController.viewStats(developer);
					break;
				case "4":
					fileComplaint(developer);
					break;
				case "0":
					return;
				default:
					System.out.println("Invalid choice.");
			}
		}
	}

	private static void supervisorMenu(Supervisor supervisor,
			VerificationController verificationController, ComplaintManagementController complaintController) {
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
				case "1":
					verificationController.showMenu(supervisor);
					break;
				case "2":
					complaintController.showDashboard(supervisor);
					break;
				case "0":
					return;
				default:
					System.out.println("Invalid choice.");
			}
		}
	}

	private static void fileComplaint(Developer developer) {
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
			System.out.println("❌ Model not found.");
			return;
		}

		System.out.print("Describe the issue: ");
		String description = scanner.nextLine().trim();

		List<String> promptLogs = sessionService.getRecentLogs(developer, model);
		if (!promptLogs.isEmpty()) {
			System.out.println("✅ Logs from last session attached (" + promptLogs.size() + " interactions).");
		}

		int nextId = complaintDao.findAll().size() + 1;
		Complaint complaint = Complaint.submit(nextId, developer, model, description, promptLogs);
		complaintDao.save(complaint);

		System.out.println("✅ Complaint #" + complaint.getId() + " filed. Pending review.");
	}
}
