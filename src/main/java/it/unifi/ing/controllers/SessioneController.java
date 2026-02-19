package it.unifi.ing.controllers;

import it.unifi.ing.business.services.ModelService;
import it.unifi.ing.business.services.SessionService;
import it.unifi.ing.domain.Developer;
import it.unifi.ing.domain.Modello;
import it.unifi.ing.domain.Sessione;
import it.unifi.ing.domain.StatoModello;

import java.util.List;
import java.util.Scanner;

/**
 * Controller per la gestione delle sessioni di chat.
 * Usato dal Developer.
 */
public class SessioneController {

	private final SessionService sessionService;
	private final ModelService modelService;
	private final Scanner scanner;

	public SessioneController(SessionService sessionService, ModelService modelService, Scanner scanner) {
		this.sessionService = sessionService;
		this.modelService = modelService;
		this.scanner = scanner;
	}

	/**
	 * Avvia una nuova sessione di chat con un modello.
	 */
	public void avviaSessione(Developer developer) {
		List<Modello> modelli = modelService.getApprovedModels();
		if (modelli.isEmpty()) {
			System.out.println("Nessun modello approvato disponibile.");
			return;
		}

		System.out.println("\n--- MODELLI DISPONIBILI ---");
		for (Modello m : modelli) {
			System.out.println("  ID: " + m.getId() + " | " + m.getNome()
					+ " | Costo/token: €" + String.format("%.4f", m.getCostoTotalePerToken()));
		}

		System.out.print("\nSeleziona ID modello (0 per tornare): ");
		int id;
		try {
			id = Integer.parseInt(scanner.nextLine().trim());
		} catch (NumberFormatException e) {
			System.out.println("ID non valido.");
			return;
		}
		if (id == 0)
			return;

		Modello modello = modelService.findById(id);
		if (modello == null || modello.getStato() != StatoModello.APPROVATO) {
			System.out.println("❌ Modello non disponibile.");
			return;
		}

		// Verifica saldo
		if (developer.getWallet().getSaldo() <= 0) {
			System.out.println("❌ Saldo insufficiente. Ricarica il tuo credito prima.");
			return;
		}

		Sessione sessione = sessionService.openSession(developer, modello);
		if (sessione == null) {
			System.out.println("❌ Nessuna GPU disponibile al momento. Riprova più tardi.");
			return;
		}

		System.out.println("✅ Sessione avviata! (ID: " + sessione.getId()
				+ ", GPU: " + sessione.getGpu().getId() + ")");
		System.out.println("Digita i tuoi messaggi. Scrivi '/esci' per terminare la sessione.\n");

		gestisciChat(sessione);
	}

	private void gestisciChat(Sessione sessione) {
		while (sessione.isAttiva()) {
			System.out.print("Tu > ");
			String input = scanner.nextLine().trim();

			if ("/esci".equalsIgnoreCase(input)) {
				sessionService.closeSession(sessione);
				System.out.println("\n📊 Sessione terminata.");
				System.out.println("   Token utilizzati: " + sessione.getTotalTokensUsed());
				System.out.println("   Costo totale: €" + String.format("%.4f", sessione.getTotalCost()));
				System.out.println("   Saldo rimanente: €" + String.format("%.2f",
						sessione.getUtente().getWallet().getSaldo()));
				return;
			}

			if (input.isEmpty())
				continue;

			String risposta = sessionService.sendPrompt(sessione, input);
			System.out.println("AI > " + risposta);
		}

		System.out.println("La sessione è stata terminata.");
	}
}
