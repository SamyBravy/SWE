package it.unifi.ing.controllers;

import it.unifi.ing.business.services.ModelService;
import it.unifi.ing.domain.Modello;
import it.unifi.ing.domain.ModelProvider;

import java.util.List;
import java.util.Scanner;

/**
 * Controller per la gestione dei modelli AI (pubblicazione).
 * Usato dal ModelProvider.
 */
public class GestioneModelliController {

	private final ModelService modelService;
	private final Scanner scanner;

	public GestioneModelliController(ModelService modelService, Scanner scanner) {
		this.modelService = modelService;
		this.scanner = scanner;
	}

	/**
	 * Mostra il menu per il ModelProvider.
	 */
	public void mostraMenu(ModelProvider provider) {
		while (true) {
			System.out.println("\n╔══════════════════════════════════════╗");
			System.out.println("║   MENU MODEL PROVIDER                ║");
			System.out.println("╠══════════════════════════════════════╣");
			System.out.println("║  1. Pubblica Modello                 ║");
			System.out.println("║  2. I miei Modelli                   ║");
			System.out.println("║  0. Logout                           ║");
			System.out.println("╚══════════════════════════════════════╝");
			System.out.print("Scelta: ");

			String scelta = scanner.nextLine().trim();

			switch (scelta) {
				case "1":
					pubblicaModello(provider);
					break;
				case "2":
					visualizzaModelli();
					break;
				case "0":
					return;
				default:
					System.out.println("Scelta non valida.");
			}
		}
	}

	private void pubblicaModello(ModelProvider provider) {
		System.out.println("\n--- PUBBLICA MODELLO ---");
		System.out.print("Nome del modello: ");
		String nome = scanner.nextLine().trim();
		System.out.print("Descrizione: ");
		String desc = scanner.nextLine().trim();
		System.out.print("Costo per token provider (€): ");
		double costoPerTokenProvider;
		try {
			costoPerTokenProvider = Double.parseDouble(scanner.nextLine().trim());
		} catch (NumberFormatException e) {
			System.out.println("❌ Costo non valido.");
			return;
		}
		System.out.print("Path file safetensors: ");
		String safetensors = scanner.nextLine().trim();
		System.out.print("Path file JSON config: ");
		String json = scanner.nextLine().trim();

		modelService.publishModel(provider, nome, desc, costoPerTokenProvider, safetensors, json);
		System.out.println("✅ Modello '" + nome + "' pubblicato con successo! In attesa di verifica.");
	}

	private void visualizzaModelli() {
		List<Modello> modelli = modelService.getAllModels();
		if (modelli.isEmpty()) {
			System.out.println("Nessun modello presente nel sistema.");
			return;
		}
		System.out.println("\n--- LISTA MODELLI ---");
		for (Modello m : modelli) {
			System.out.println("  " + m);
		}
	}
}
