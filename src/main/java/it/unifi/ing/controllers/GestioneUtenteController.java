package it.unifi.ing.controllers;

import it.unifi.ing.dao.interfaces.SessioneDAO;
import it.unifi.ing.domain.Developer;
import it.unifi.ing.domain.Sessione;

import java.util.List;
import java.util.Scanner;

/**
 * Controller per la gestione utente: ricarica credito e visualizzazione
 * statistiche.
 */
public class GestioneUtenteController {

	private final SessioneDAO sessioneDao;
	private final Scanner scanner;

	public GestioneUtenteController(SessioneDAO sessioneDao, Scanner scanner) {
		this.sessioneDao = sessioneDao;
		this.scanner = scanner;
	}

	/**
	 * Ricarica il credito del developer.
	 */
	public void ricaricaCredito(Developer developer) {
		System.out.println("\n--- RICARICA CREDITO ---");
		System.out.println("Saldo attuale: €" + String.format("%.2f", developer.getWallet().getSaldo()));
		System.out.print("Importo da ricaricare (€): ");

		try {
			double importo = Double.parseDouble(scanner.nextLine().trim());
			if (importo <= 0) {
				System.out.println("❌ L'importo deve essere positivo.");
				return;
			}
			developer.getWallet().addCredito(importo);
			System.out.println("✅ Ricarica di €" + String.format("%.2f", importo) + " effettuata!");
			System.out.println("   Nuovo saldo: €" + String.format("%.2f", developer.getWallet().getSaldo()));
		} catch (NumberFormatException e) {
			System.out.println("❌ Importo non valido.");
		}
	}

	/**
	 * Visualizza le statistiche dell'utente.
	 */
	public void visualizzaStatistiche(Developer developer) {
		System.out.println("\n╔══════════════════════════════════════╗");
		System.out.println("║   STATISTICHE UTENTE                 ║");
		System.out.println("╚══════════════════════════════════════╝");
		System.out.println("  Nome: " + developer.getNome());
		System.out.println("  Email: " + developer.getEmail());
		System.out.println("  Saldo: €" + String.format("%.2f", developer.getWallet().getSaldo()));

		List<Sessione> sessioni = sessioneDao.findByUtente(developer.getId());
		int totaleSessioni = sessioni.size();
		int sessioniAttive = (int) sessioni.stream().filter(Sessione::isAttiva).count();
		int totalTokens = sessioni.stream().mapToInt(Sessione::getTotalTokensUsed).sum();

		System.out.println("\n  📊 Statistiche Sessioni:");
		System.out.println("  - Sessioni totali: " + totaleSessioni);
		System.out.println("  - Sessioni attive: " + sessioniAttive);
		System.out.println("  - Token totali utilizzati: " + totalTokens);

		if (!sessioni.isEmpty()) {
			System.out.println("\n  📋 Storico Sessioni:");
			for (Sessione s : sessioni) {
				System.out.println("    " + s);
			}
		}

		List<String> transazioni = developer.getWallet().getStoricoTransazioniStringhe();
		if (!transazioni.isEmpty()) {
			System.out.println("\n  💰 Storico Transazioni:");
			for (String t : transazioni) {
				System.out.println("    " + t);
			}
		}
	}
}
