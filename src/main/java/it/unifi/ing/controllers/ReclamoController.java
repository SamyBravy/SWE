package it.unifi.ing.controllers;

import it.unifi.ing.business.services.ComplaintService;
import it.unifi.ing.domain.Reclamo;
import it.unifi.ing.domain.StatoReclamo;
import it.unifi.ing.domain.Supervisor;

import java.util.List;
import java.util.Scanner;

/**
 * Controller per la gestione dei reclami.
 * Usato dal Supervisor per revisionare, accettare o rifiutare reclami.
 */
public class ReclamoController {

    private final ComplaintService complaintService;
    private final Scanner scanner;

    public ReclamoController(ComplaintService complaintService, Scanner scanner) {
        this.complaintService = complaintService;
        this.scanner = scanner;
    }

    /**
     * Mostra la dashboard reclami per il Supervisor.
     */
    public void mostraDashboard(Supervisor supervisor) {
        while (true) {
            System.out.println("\n╔══════════════════════════════════════╗");
            System.out.println("║   DASHBOARD RECLAMI                  ║");
            System.out.println("╠══════════════════════════════════════╣");
            System.out.println("║  1. Reclami in attesa                ║");
            System.out.println("║  2. Tutti i reclami                  ║");
            System.out.println("║  0. Torna al menu                    ║");
            System.out.println("╚══════════════════════════════════════╝");
            System.out.print("Scelta: ");

            String scelta = scanner.nextLine().trim();

            switch (scelta) {
                case "1":
                    gestisciReclamiInAttesa();
                    break;
                case "2":
                    visualizzaTuttiReclami();
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Scelta non valida.");
            }
        }
    }

    private void gestisciReclamiInAttesa() {
        List<Reclamo> pendenti = complaintService.getPendingComplaints();
        if (pendenti.isEmpty()) {
            System.out.println("Nessun reclamo in attesa di revisione.");
            return;
        }

        System.out.println("\n--- RECLAMI IN ATTESA ---");
        for (Reclamo r : pendenti) {
            System.out.println("  ID: " + r.getId() + " | Developer: " + r.getDeveloper().getNome()
                    + " | Modello: " + r.getModello().getNome());
        }

        System.out.print("\nSeleziona ID reclamo da revisionare (0 per tornare): ");
        int id;
        try {
            id = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("ID non valido.");
            return;
        }
        if (id == 0) return;

        Reclamo reclamo = complaintService.findById(id);
        if (reclamo == null || reclamo.getStato() != StatoReclamo.IN_ATTESA) {
            System.out.println("Reclamo non trovato o già revisionato.");
            return;
        }

        revisionaReclamo(reclamo);
    }

    private void revisionaReclamo(Reclamo reclamo) {
        System.out.println("\n=== REVISIONE RECLAMO #" + reclamo.getId() + " ===");
        System.out.println("  Developer: " + reclamo.getDeveloper().getNome());
        System.out.println("  Modello: " + reclamo.getModello().getNome());
        System.out.println("  Descrizione: " + reclamo.getDescrizione());

        System.out.println("\n  📋 Log Prompt:");
        for (String log : reclamo.getPromptLogs()) {
            System.out.println("    " + log);
        }

        System.out.println("\nDecisione:");
        System.out.println("  1. Accetta reclamo");
        System.out.println("  2. Rifiuta reclamo");
        System.out.print("Scelta: ");
        String decisione = scanner.nextLine().trim();

        if ("1".equals(decisione)) {
            // Token restituiti
            System.out.print("Token da restituire al developer: ");
            int tokenRestituiti;
            try {
                tokenRestituiti = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Valore non valido.");
                return;
            }

            // Blocco modello (opzionale)
            System.out.print("Bloccare il modello? (s/n): ");
            String blocco = scanner.nextLine().trim().toLowerCase();
            int bloccoOre = 0;
            if ("s".equals(blocco)) {
                System.out.print("Durata del blocco (ore): ");
                try {
                    bloccoOre = Integer.parseInt(scanner.nextLine().trim());
                } catch (NumberFormatException e) {
                    System.out.println("Valore non valido, blocco ignorato.");
                }
            }

            complaintService.acceptComplaint(reclamo, tokenRestituiti, bloccoOre);
            System.out.println("✅ Reclamo #" + reclamo.getId() + " accettato."
                    + (tokenRestituiti > 0 ? " Token rimborsati: " + tokenRestituiti : ""));

        } else if ("2".equals(decisione)) {
            System.out.print("Motivi del rifiuto: ");
            String motivi = scanner.nextLine().trim();
            complaintService.rejectComplaint(reclamo, motivi);
            System.out.println("❌ Reclamo #" + reclamo.getId() + " rifiutato.");
        }
    }

    private void visualizzaTuttiReclami() {
        List<Reclamo> tutti = complaintService.getAllComplaints();
        if (tutti.isEmpty()) {
            System.out.println("Nessun reclamo nel sistema.");
            return;
        }
        System.out.println("\n--- TUTTI I RECLAMI ---");
        for (Reclamo r : tutti) {
            System.out.println("  " + r);
        }
    }
}
