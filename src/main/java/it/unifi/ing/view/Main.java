package it.unifi.ing.view;

import it.unifi.ing.business.Timer;
import it.unifi.ing.business.services.*;
import it.unifi.ing.controllers.*;
import it.unifi.ing.dao.interfaces.*;
import it.unifi.ing.dao.memory.*;
import it.unifi.ing.domain.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Main: entry point dell'applicazione CLI.
 * Gestisce il bootstrapping e il routing basato sul ruolo dell'utente.
 */
public class Main {

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);

		// ===== 1. BOOTSTRAPPING: Istanzia i DAO in memoria =====
		UtenteDAO utenteDao = new InMemoryUtenteDAO();
		ModelloDAO modelloDao = new InMemoryModelloDAO();
		SessioneDAO sessioneDao = new InMemorySessioneDAO();
		GpuDAO gpuDao = new InMemoryGpuDAO();
		ReclamoDAO reclamoDao = new InMemoryReclamoDAO();

		// ===== 2. INIZIALIZZA IL CLUSTER GPU =====
		// Crea e registra le GPU disponibili
		for (int i = 1; i <= 4; i++) {
			gpuDao.save(new GPU(i));
		}

		ClusterGPU cluster = ClusterGPU.getInstance();
		cluster.init(gpuDao);

		// ===== 3. CREA I SERVIZI =====
		AuthService authService = new AuthService(utenteDao);
		ModelService modelService = new ModelService(modelloDao);
		BillingStrategy billingStrategy = new StandardBillingStrategy();
		BillingService billingService = new BillingService(billingStrategy, sessioneDao);
		SessionService sessionService = new SessionService(sessioneDao, cluster, billingService);
		VerificationService verificationService = new VerificationService(modelloDao, cluster);
		ComplaintService complaintService = new ComplaintService(reclamoDao, utenteDao);

		// LoadBalancerService (Observer) — registra su tutte le GPU
		LoadBalancerService loadBalancer = new LoadBalancerService(sessioneDao, cluster, billingService);
		loadBalancer.registraSuTutteLeGpu();

		// ===== 4. AVVIA IL TIMER =====
		Timer timer = Timer.getInstance(cluster);

		// Disabilita il billing periodico (passando null) per usare il billing real-time
		timer.configuraServizi(null, loadBalancer);
		timer.avvia();

		// ===== 5. CREA I CONTROLLER =====
		AuthController authController = new AuthController(authService, scanner);
		GestioneModelliController gestioneModelliController = new GestioneModelliController(modelService, scanner);
		VerificaController verificaController = new VerificaController(verificationService, modelService, scanner);
		SessioneController sessioneController = new SessioneController(sessionService, modelService, scanner);
		GestioneUtenteController gestioneUtenteController = new GestioneUtenteController(sessioneDao, scanner);
		ReclamoController reclamoController = new ReclamoController(complaintService, scanner);

		// ===== 6. LOOP PRINCIPALE =====
		System.out.println("═══════════════════════════════════════");
		System.out.println("  GESTIONE CLUSTER GPU v1.0");
		System.out.println("  Sistema di gestione risorse GPU");
		System.out.println("═══════════════════════════════════════");

		while (true) {
			// Autenticazione
			Utente utente = authController.mostraMenu();

			// Routing basato sul ruolo
			if (utente instanceof Developer) {
				menuDeveloper((Developer) utente, sessioneController, gestioneUtenteController,
						reclamoDao, modelService, sessionService, scanner);
			} else if (utente instanceof ModelProvider) {
				gestioneModelliController.mostraMenu((ModelProvider) utente);
			} else if (utente instanceof Supervisor) {
				menuSupervisor((Supervisor) utente, verificaController, reclamoController, scanner);
			}
		}
	}

	/**
	 * Menu principale per il Developer.
	 */
	private static void menuDeveloper(Developer developer,
			SessioneController sessioneController,
			GestioneUtenteController gestioneUtenteController,
			ReclamoDAO reclamoDao,
			ModelService modelService,
			SessionService sessionService,
			Scanner scanner) {
		while (true) {
			System.out.println("\n╔══════════════════════════════════════╗");
			System.out.println("║   MENU DEVELOPER                     ║");
			System.out.println("╠══════════════════════════════════════╣");
			System.out.println("║  1. Chat con Modello                 ║");
			System.out.println("║  2. Ricarica Credito                 ║");
			System.out.println("║  3. Visualizza Statistiche           ║");
			System.out.println("║  4. Invia Reclamo                    ║");
			System.out.println("║  0. Logout                           ║");
			System.out.println("╚══════════════════════════════════════╝");
			System.out.println("  Saldo: €" + String.format("%.2f", developer.getWallet().getSaldo()));
			System.out.print("Scelta: ");

			String scelta = scanner.nextLine().trim();

			switch (scelta) {
				case "1":
					sessioneController.avviaSessione(developer);
					break;
				case "2":
					gestioneUtenteController.ricaricaCredito(developer);
					break;
				case "3":
					gestioneUtenteController.visualizzaStatistiche(developer);
					break;
				case "4":
					inviaReclamo(developer, reclamoDao, modelService, sessionService, scanner);
					break;
				case "0":
					return;
				default:
					System.out.println("Scelta non valida.");
			}
		}
	}

	/**
	 * Funzionalità per l'invio di un reclamo da parte del Developer.
	 */
	private static void inviaReclamo(Developer developer, ReclamoDAO reclamoDao,
			ModelService modelService, SessionService sessionService, Scanner scanner) {
		List<Modello> modelli = modelService.getApprovedModels();
		if (modelli.isEmpty()) {
			System.out.println("Nessun modello disponibile.");
			return;
		}

		System.out.println("\n--- INVIA RECLAMO ---");
		System.out.println("Seleziona il modello oggetto del reclamo:");
		for (Modello m : modelli) {
			System.out.println("  ID: " + m.getId() + " | " + m.getNome());
		}

		System.out.print("ID modello: ");
		int id;
		try {
			id = Integer.parseInt(scanner.nextLine().trim());
		} catch (NumberFormatException e) {
			System.out.println("ID non valido.");
			return;
		}

		Modello modello = modelService.findById(id);
		if (modello == null) {
			System.out.println("Modello non trovato.");
			return;
		}

		System.out.print("Descrizione del reclamo: ");
		String descrizione = scanner.nextLine().trim();

		// Recupera i log dell'ultima sessione
		List<String> promptLogs = sessionService.getRecentLogs(developer, modello);
		if (promptLogs.isEmpty()) {
			System.out.println("⚠️  Nessuna sessione recente trovata con questo modello. I log saranno vuoti.");
			promptLogs = new ArrayList<>();
		} else {
			System.out.println("✅ Log dell'ultima sessione allegati (" + promptLogs.size() + " interazioni).");
		}

		int nextReclamoId = reclamoDao.findAll().size() + 1;
		Reclamo reclamo = new Reclamo(nextReclamoId, developer, modello, descrizione, promptLogs);
		reclamoDao.save(reclamo);

		System.out.println("✅ Reclamo #" + reclamo.getId() + " inviato con successo.");
	}

	/**
	 * Menu principale per il Supervisor.
	 */
	private static void menuSupervisor(Supervisor supervisor,
			VerificaController verificaController,
			ReclamoController reclamoController,
			Scanner scanner) {
		while (true) {
			System.out.println("\n╔══════════════════════════════════════╗");
			System.out.println("║   MENU SUPERVISOR                    ║");
			System.out.println("╠══════════════════════════════════════╣");
			System.out.println("║  1. Verifica Modelli in Attesa       ║");
			System.out.println("║  2. Gestisci Reclami                 ║");
			System.out.println("║  3. Visualizza tutti i Modelli       ║");
			System.out.println("║  0. Logout                           ║");
			System.out.println("╚══════════════════════════════════════╝");
			System.out.print("Scelta: ");

			String scelta = scanner.nextLine().trim();

			switch (scelta) {
				case "1":
					verificaController.verificaModelli();
					break;
				case "2":
					reclamoController.mostraDashboard(supervisor);
					break;
				case "3":
					verificaController.visualizzaTuttiModelli();
					break;
				case "0":
					return;
				default:
					System.out.println("Scelta non valida.");
			}
		}
	}
}
