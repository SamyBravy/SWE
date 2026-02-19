package it.unifi.ing.controllers;

import it.unifi.ing.business.services.AuthService;
import it.unifi.ing.domain.Utente;

import java.util.Scanner;

/**
 * Controller per l'autenticazione: gestisce il menu iniziale di
 * Login/Registrazione via CLI.
 */
public class AuthController {

	private final AuthService authService;
	private final Scanner scanner;

	public AuthController(AuthService authService, Scanner scanner) {
		this.authService = authService;
		this.scanner = scanner;
	}

	/**
	 * Mostra il menu iniziale e gestisce login/registrazione.
	 * 
	 * @return l'utente autenticato
	 */
	public Utente mostraMenu() {
		while (true) {
			System.out.println("\n╔══════════════════════════════════════╗");
			System.out.println("║   GESTIONE CLUSTER GPU - Benvenuto   ║");
			System.out.println("╠══════════════════════════════════════╣");
			System.out.println("║  1. Login                            ║");
			System.out.println("║  2. Registrazione                    ║");
			System.out.println("║  0. Esci                             ║");
			System.out.println("╚══════════════════════════════════════╝");
			System.out.print("Scelta: ");

			String scelta = scanner.nextLine().trim();

			switch (scelta) {
				case "1":
					Utente utenteLogin = gestisciLogin();
					if (utenteLogin != null)
						return utenteLogin;
					break;
				case "2":
					Utente utenteReg = gestisciRegistrazione();
					if (utenteReg != null)
						return utenteReg;
					break;
				case "0":
					System.out.println("Arrivederci!");
					System.exit(0);
					break;
				default:
					System.out.println("Scelta non valida.");
			}
		}
	}

	private Utente gestisciLogin() {
		System.out.println("\n--- LOGIN ---");
		System.out.print("Email: ");
		String email = scanner.nextLine().trim();
		System.out.print("Password: ");
		String password = scanner.nextLine().trim();

		Utente utente = authService.login(email, password);
		if (utente != null) {
			System.out.println("✅ Login riuscito! Benvenuto, " + utente.getNome()
					+ " (" + utente.getRuolo() + ")");
			return utente;
		} else {
			System.out.println("❌ Credenziali non valide.");
			return null;
		}
	}

	private Utente gestisciRegistrazione() {
		System.out.println("\n--- REGISTRAZIONE ---");
		System.out.print("Nome: ");
		String nome = scanner.nextLine().trim();
		System.out.print("Email: ");
		String email = scanner.nextLine().trim();
		System.out.print("Password: ");
		String password = scanner.nextLine().trim();

		System.out.println("Seleziona ruolo:");
		System.out.println("  1. Developer");
		System.out.println("  2. ModelProvider");
		System.out.println("  3. Supervisor");
		System.out.print("Scelta: ");
		String sceltaRuolo = scanner.nextLine().trim();

		String ruolo;
		switch (sceltaRuolo) {
			case "1":
				ruolo = "developer";
				break;
			case "2":
				ruolo = "modelprovider";
				break;
			case "3":
				ruolo = "supervisor";
				break;
			default:
				System.out.println("Ruolo non valido.");
				return null;
		}

		Utente utente = authService.registra(nome, email, password, ruolo);
		if (utente != null) {
			System.out.println("✅ Registrazione completata! Benvenuto, " + utente.getNome()
					+ " (" + utente.getRuolo() + ")");
			return utente;
		} else {
			System.out.println("❌ Email già registrata o ruolo non valido.");
			return null;
		}
	}
}
