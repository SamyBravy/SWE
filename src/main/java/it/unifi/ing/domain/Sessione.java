package it.unifi.ing.domain;

import java.time.LocalDateTime;

/**
 * Sessione: collega un Developer, un Modello e una GPU fisica.
 */
public class Sessione {

    private int id;
    private Developer utente;
    private Modello modello;
    private GPU gpu;
    private LocalDateTime inizio;
    private LocalDateTime fine;
    private boolean attiva;
    private int tokensUsed;

    public Sessione(int id, Developer utente, Modello modello, GPU gpu) {
        this.id = id;
        this.utente = utente;
        this.modello = modello;
        this.gpu = gpu;
        this.inizio = LocalDateTime.now();
        this.fine = null;
        this.attiva = true;
        this.tokensUsed = 0;
        this.interactionLog = new java.util.ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Developer getUtente() {
        return utente;
    }

    public void setUtente(Developer utente) {
        this.utente = utente;
    }

    public Modello getModello() {
        return modello;
    }

    public void setModello(Modello modello) {
        this.modello = modello;
    }

    public GPU getGpu() {
        return gpu;
    }

    public void setGpu(GPU gpu) {
        this.gpu = gpu;
    }

    public LocalDateTime getInizio() {
        return inizio;
    }

    public void setInizio(LocalDateTime inizio) {
        this.inizio = inizio;
    }

    public LocalDateTime getFine() {
        return fine;
    }

    public void setFine(LocalDateTime fine) {
        this.fine = fine;
    }

    public boolean isAttiva() {
        return attiva;
    }

    public void setAttiva(boolean attiva) {
        this.attiva = attiva;
    }

    private int totalTokensUsed;
    private double totalCost;

    public int getTokensUsed() {
        return tokensUsed;
    }

    public void setTokensUsed(int tokensUsed) {
        this.tokensUsed = tokensUsed;
    }

    public int getTotalTokensUsed() {
        return totalTokensUsed;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void addTokens(int tokens) {
        this.tokensUsed += tokens;
        this.totalTokensUsed += tokens;
    }

    public void addTotalCost(double cost) {
        this.totalCost += cost;
    }

    /**
     * Chiude la sessione, segnando il timestamp di fine.
     */
    public void chiudi() {
        this.attiva = false;
        this.fine = LocalDateTime.now();
    }

    private java.util.List<String> interactionLog;

    public void addLog(String log) {
        this.interactionLog.add(log);
    }

    public java.util.List<String> getInteractionLog() {
        return java.util.Collections.unmodifiableList(interactionLog);
    }

    @Override
    public String toString() {
        return "Sessione [id=" + id + ", utente=" + utente.getNome()
                + ", modello=" + modello.getNome() + ", gpu=" + gpu.getId()
                + ", attiva=" + attiva + ", tokens=" + totalTokensUsed + "]";
    }
}
