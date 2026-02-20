package it.unifi.ing.controllers;

import it.unifi.ing.business.services.ModelService;
import it.unifi.ing.business.services.SessionService;
import it.unifi.ing.domain.AiModel;
import it.unifi.ing.domain.Developer;
import it.unifi.ing.domain.ModelStatus;
import it.unifi.ing.domain.Session;

import java.util.List;
import java.util.Scanner;

/**
 * Controller for AI chat sessions.
 */
public class SessionController {

	private final SessionService sessionService;
	private final ModelService modelService;
	private final Scanner scanner;

	public SessionController(SessionService sessionService, ModelService modelService, Scanner scanner) {
		this.sessionService = sessionService;
		this.modelService = modelService;
		this.scanner = scanner;
	}

	public void startSession(Developer developer) {
		List<AiModel> models = modelService.getApprovedModels();
		if (models.isEmpty()) {
			System.out.println("No approved models available.");
			return;
		}

		System.out.println("\n--- AVAILABLE MODELS ---");
		for (AiModel m : models) {
			System.out.println("  ID: " + m.getId() + " | " + m.getName()
					+ " | Cost/token: €" + String.format("%.4f", m.getCostPerToken())
					+ " | Description: " + m.getDescription());
		}

		System.out.print("\nSelect model ID (0 to go back): ");
		int id;
		try {
			id = Integer.parseInt(scanner.nextLine().trim());
		} catch (NumberFormatException e) {
			System.out.println("Invalid ID.");
			return;
		}
		if (id == 0) return;

		AiModel model = modelService.findById(id);
		if (model == null || model.getStatus() != ModelStatus.APPROVED) {
			System.out.println("❌ Model not available.");
			return;
		}

		Session session = sessionService.openSession(developer, model);
		if (session == null) {
			System.out.println("❌ No GPU available. Try again later.");
			return;
		}

		System.out.println("✅ Session started! (ID: " + session.getId() + ", GPU: " + session.getGpu().getId() + ")");
		System.out.println("Type your messages. Type '/exit' to end the session.");

		handleChat(developer, session);
	}

	private void handleChat(Developer developer, Session session) {
		while (session.isActive()) {
			System.out.print("\nYou > ");
			String prompt = scanner.nextLine().trim();

			if ("/exit".equalsIgnoreCase(prompt)) {
				double totalCost = sessionService.closeSession(session);
				System.out.println("\n🔚 Session ended.");
				System.out.println("   Total tokens used: " + session.getTotalTokensUsed());
				System.out.println("   Total cost: €" + String.format("%.4f", session.getTotalCost()));
				System.out.println("   Remaining balance: €" + String.format("%.2f", developer.getWallet().getBalance()));
				return;
			}

			if (prompt.isEmpty()) {
				continue;
			}

			String response = sessionService.sendPrompt(session, prompt);
			System.out.println("AI > " + response);
		}
	}
}
