package it.unifi.ing.controllers;

import it.unifi.ing.business.services.ModelService;
import it.unifi.ing.business.services.VerificationService;
import it.unifi.ing.domain.AiModel;
import it.unifi.ing.domain.GPU;
import it.unifi.ing.domain.ModelStatus;
import it.unifi.ing.domain.Supervisor;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Controller for AI model verification by the Supervisor.
 */
public class VerificationController {

	private final VerificationService verificationService;
	private final ModelService modelService;
	private final ModelController modelController;
	private final Scanner scanner;

	public VerificationController(VerificationService verificationService,
			ModelService modelService, ModelController modelController, Scanner scanner) {
		this.verificationService = verificationService;
		this.modelService = modelService;
		this.modelController = modelController;
		this.scanner = scanner;
	}

	public void showMenu(Supervisor supervisor) {
		while (true) {
			System.out.println("\n╔══════════════════════════════════════╗");
			System.out.println("║   VERIFICATION DASHBOARD             ║");
			System.out.println("╠══════════════════════════════════════╣");
			System.out.println("║  1. Verify pending models            ║");
			System.out.println("║  2. View all models                  ║");
			System.out.println("║  3. Unblock models                   ║");
			System.out.println("║  0. Back                             ║");
			System.out.println("╚══════════════════════════════════════╝");
			System.out.print("Choice: ");

			String choice = scanner.nextLine().trim();

			switch (choice) {
				case "1" -> verifyPendingModels(supervisor);
				case "2" -> modelController.viewModels();
				case "3" -> unblockModels(supervisor);
				case "0" -> {
					return;
				}
				default -> System.out.println("Invalid choice.");
			}
		}
	}

	private void verifyPendingModels(Supervisor supervisor) {
		List<AiModel> pending = modelService.getPendingModels();
		if (pending.isEmpty()) {
			System.out.println("No models pending review.");
			return;
		}

		System.out.println("\n--- MODELS PENDING REVIEW ---");
		for (AiModel m : pending) {
			System.out.println("  ID: " + m.getId() + " | " + m.getName()
					+ " | Provider: " + m.getProvider().getName()
					+ " | Provider cost/token: €" + String.format("%.4f", m.getCostPerTokenProvider())
					+ " | Description: " + m.getDescription());
		}

		System.out.print("\nSelect model ID to verify (0 to go back): ");
		int id;
		try {
			id = Integer.parseInt(scanner.nextLine().trim());
		} catch (NumberFormatException e) {
			System.out.println("Invalid ID.");
			return;
		}
		if (id == 0)
			return;

		AiModel model = modelService.findById(id);
		if (model == null) {
			System.out.println("Model not found.");
			return;
		}

		GPU gpu = verificationService.loadOnGpu(model);
		if (gpu == null) {
			System.out.println("No GPU available for verification.");
			return;
		}
		System.out.println("Model loaded on GPU " + gpu.getId());

		Map<String, Object> results = verificationService.runBenchmarks(model, gpu);
		System.out.println("\n📊 Benchmark Results:");
		for (Map.Entry<String, Object> entry : results.entrySet()) {
			System.out.println("  - " + entry.getKey() + ": " + entry.getValue());
		}

		System.out.println("\n🧪 Automated Ethics Tests:");
		boolean[] ethicsResults = verificationService.runAutomatedEthicsTests(model);
		for (int i = 0; i < ethicsResults.length; i++) {
			System.out.println("  - Test " + (i + 1) + ": " + (ethicsResults[i] ? "✅ PASSED" : "❌ FAILED"));
		}

		System.out.println("\n🧪 Manual Ethics Test:");
		System.out.print("Enter test prompt (or press enter to skip): ");
		String testPrompt = scanner.nextLine().trim();
		if (!testPrompt.isEmpty()) {
			String response = model.generateResponse(testPrompt);
			System.out.println("  Response: " + response);
		}

		System.out.println("\nDecision:");
		System.out.println("  1. Approve");
		System.out.println("  2. Reject");
		System.out.print("Choice: ");
		String decision = scanner.nextLine().trim();

		if ("1".equals(decision)) {
			System.out.print("Platform cost per token (€): ");
			try {
				double platformCost = Double.parseDouble(scanner.nextLine().trim());
				verificationService.approveModel(model, platformCost);
				System.out.println("Model '" + model.getName() + "' approved! Total cost/token: €"
						+ String.format("%.4f", model.getCostPerToken()));
			} catch (NumberFormatException e) {
				System.out.println("Invalid cost.");
			}
		} else if ("2".equals(decision)) {
			System.out.print("Rejection reason: ");
			String reason = scanner.nextLine().trim();
			verificationService.rejectModel(model, reason);
			System.out.println("Model '" + model.getName() + "' rejected.");
		}

		verificationService.releaseGpu(gpu);
	}

	private void unblockModels(Supervisor supervisor) {
		List<AiModel> blocked = modelService.getBlockedModels();
		if (blocked.isEmpty()) {
			System.out.println("No blocked models.");
			return;
		}

		System.out.println("\n--- BLOCKED MODELS ---");
		for (AiModel m : blocked) {
			System.out.println("  ID: " + m.getId() + " | " + m.getName()
					+ " | Provider: " + m.getProvider().getName());
		}

		System.out.print("\nSelect model ID to unblock (0 to go back): ");
		int id;
		try {
			id = Integer.parseInt(scanner.nextLine().trim());
		} catch (NumberFormatException e) {
			System.out.println("Invalid ID.");
			return;
		}
		if (id == 0)
			return;

		AiModel model = modelService.findById(id);
		if (model == null || model.getStatus() != ModelStatus.BLOCKED) {
			System.out.println("Model not found or not blocked.");
			return;
		}

		verificationService.unblockModel(model);
		System.out.println("Model '" + model.getName() + "' successfully unblocked and approved.");
	}

}
