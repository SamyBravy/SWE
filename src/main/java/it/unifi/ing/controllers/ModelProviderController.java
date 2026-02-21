package it.unifi.ing.controllers;

import it.unifi.ing.business.services.ModelService;
import it.unifi.ing.domain.AiModel;
import it.unifi.ing.domain.ModelProvider;
import it.unifi.ing.domain.ModelStatus;

import java.util.List;
import java.util.Scanner;

/**
 * Controller for AI model management (publishing).
 */
public class ModelProviderController {

	private final ModelService modelService;
	private final Scanner scanner;

	public ModelProviderController(ModelService modelService, Scanner scanner) {
		this.modelService = modelService;
		this.scanner = scanner;
	}

	public void showMenu(ModelProvider provider) {
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
				case "1" -> publishModel(provider);
				case "2" -> viewModels();
				case "0" -> {
					return;
				}
				default -> System.out.println("Invalid choice.");
			}
		}
	}

	private void publishModel(ModelProvider provider) {
		System.out.println("\n--- PUBLISH MODEL ---");
		System.out.print("Model name: ");
		String name = scanner.nextLine().trim();
		System.out.print("Description: ");
		String desc = scanner.nextLine().trim();
		System.out.print("Cost per token provider (€): ");
		double costPerToken;
		try {
			costPerToken = Double.parseDouble(scanner.nextLine().trim());
		} catch (NumberFormatException e) {
			System.out.println("❌ Invalid cost.");
			return;
		}
		System.out.print("Safetensors file path: ");
		String safetensors = scanner.nextLine().trim();
		System.out.print("JSON config file path: ");
		String json = scanner.nextLine().trim();

		modelService.publishModel(provider, name, desc, costPerToken, safetensors, json);
		System.out.println("✅ Model '" + name + "' published successfully! Pending review.");
	}

	private void viewModels() {
		List<AiModel> models = modelService.getAllModels();
		if (models.isEmpty()) {
			System.out.println("No models in the system.");
			return;
		}
		System.out.println("\n--- MODEL LIST ---");
		for (AiModel m : models) {
			System.out.println("  " + m);
			if (m.getStatus() == ModelStatus.REJECTED && m.getRejectionReasons() != null) {
				System.out.println("    ❌ Rejection reason: " + m.getRejectionReasons());
			}
		}
	}
}
