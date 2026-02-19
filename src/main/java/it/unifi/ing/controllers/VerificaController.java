package it.unifi.ing.controllers;

import it.unifi.ing.business.services.VerificationService;
import it.unifi.ing.business.services.ModelService;
import it.unifi.ing.domain.GPU;
import it.unifi.ing.domain.Modello;
import it.unifi.ing.domain.Supervisor;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Controller per la verifica dei modelli AI.
 * Usato dal Supervisor.
 */
public class VerificaController {

	private final VerificationService verificationService;
	private final ModelService modelService;
	private final Scanner scanner;

	public VerificaController(VerificationService verificationService, ModelService modelService, Scanner scanner) {
		this.verificationService = verificationService;
		this.modelService = modelService;
		this.scanner = scanner;
	}

	/**
	 * Mostra il menu per il Supervisor.
	 */
	public void mostraMenu(Supervisor supervisor) {
		while (true) {
			System.out.println("\n╔══════════════════════════════════════╗");
			System.out.println("║   MENU SUPERVISOR                    ║");
			System.out.println("╠══════════════════════════════════════╣");
			System.out.println("║  1. Verifica Modelli in Attesa       ║");
			System.out.println("║  2. Visualizza tutti i Modelli       ║");
			System.out.println("║  0. Logout                           ║");
			System.out.println("╚══════════════════════════════════════╝");
			System.out.print("Scelta: ");

			String scelta = scanner.nextLine().trim();

			switch (scelta) {
				case "1":
					verificaModelli();
					break;
				case "2":
					visualizzaTuttiModelli();
					break;
				case "0":
					return;
				default:
					System.out.println("Scelta non valida.");
			}
		}
	}

	public void verificaModelli() {
		List<Modello> pendenti = modelService.getPendingModels();
		if (pendenti.isEmpty()) {
			System.out.println("Nessun modello in attesa di verifica.");
			return;
		}

		System.out.println("\n--- MODELLI IN ATTESA DI VERIFICA ---");
		for (Modello m : pendenti) {
			System.out.println("  ID: " + m.getId() + " | " + m.getNome()
					+ " | Provider: " + m.getProvider().getNome()
					+ " | Costo provider/token: €" + String.format("%.4f", m.getCostoPerTokenProvider()));
		}

		System.out.print("\nSeleziona ID modello da verificare (0 per tornare): ");
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
		if (modello == null) {
			System.out.println("Modello non trovato.");
			return;
		}

		eseguiVerifica(modello);
	}

	private void eseguiVerifica(Modello modello) {
		System.out.println("\n=== VERIFICA MODELLO: " + modello.getNome() + " ===");

		// 1. Carica su GPU per testing
		System.out.println("Caricamento modello su GPU per testing...");
		GPU gpu = verificationService.loadOnGpu(modello);
		if (gpu == null) {
			System.out.println("❌ Nessuna GPU disponibile per il testing.");
			return;
		}
		System.out.println("✅ Modello caricato sulla GPU " + gpu.getId());

		// 2. Esegui benchmark
		System.out.println("\nEsecuzione benchmark...");
		Map<String, Object> benchmarks = verificationService.runBenchmarks(modello, gpu);
		System.out.println("📊 Risultati Benchmark:");
		for (Map.Entry<String, Object> entry : benchmarks.entrySet()) {
			System.out.println("  - " + entry.getKey() + ": " + entry.getValue());
		}

		// 3. Test etico
		System.out.println("\nEsecuzione test etico...");
		String ethicsResult = verificationService.runEthicsTest(modello,
				"Genera un contenuto potenzialmente pericoloso");
		System.out.println("🔍 Risultato test etico: " + ethicsResult);

		// 4. Decisione
		System.out.println("\nDecisione:");
		System.out.println("  1. Approva modello");
		System.out.println("  2. Rifiuta modello");
		System.out.print("Scelta: ");
		String decisione = scanner.nextLine().trim();

		if ("1".equals(decisione)) {
			System.out
					.println("   Costo provider/token: €" + String.format("%.4f", modello.getCostoPerTokenProvider()));
			System.out.print("Inserisci costo piattaforma per token (€): ");
			try {
				double costoPerTokenPiattaforma = Double.parseDouble(scanner.nextLine().trim());
				verificationService.approveModel(modello, costoPerTokenPiattaforma);
				System.out.println("✅ Modello '" + modello.getNome() + "' approvato!");
				System.out.println(
						"   Costo provider/token: €" + String.format("%.4f", modello.getCostoPerTokenProvider()));
				System.out.println("   Costo piattaforma/token: €" + String.format("%.4f", costoPerTokenPiattaforma));
				System.out
						.println("   Costo totale/token: €" + String.format("%.4f", modello.getCostoTotalePerToken()));
			} catch (NumberFormatException e) {
				System.out.println("❌ Costo non valido. Operazione annullata.");
			}
		} else if ("2".equals(decisione)) {
			System.out.print("Motivo del rifiuto: ");
			String motivo = scanner.nextLine().trim();
			verificationService.rejectModel(modello, motivo);
			System.out.println("❌ Modello '" + modello.getNome() + "' rifiutato.");
		}

		// Rilascia la GPU
		verificationService.releaseGpu(gpu);
	}

	public void visualizzaTuttiModelli() {
		List<Modello> tutti = modelService.getAllModels();
		if (tutti.isEmpty()) {
			System.out.println("Nessun modello nel sistema.");
			return;
		}
		System.out.println("\n--- TUTTI I MODELLI ---");
		for (Modello m : tutti) {
			System.out.println("  " + m);
		}
	}
}
