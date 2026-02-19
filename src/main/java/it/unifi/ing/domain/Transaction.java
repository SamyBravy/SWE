package it.unifi.ing.domain;

import java.time.LocalDateTime;

/**
 * Transaction: registra una singola transazione nel Wallet.
 */
public class Transaction {

    private final int id;
    private final double importo;
    private final LocalDateTime timestamp;
    private final String motivo;

    public Transaction(int id, double importo, LocalDateTime timestamp, String motivo) {
        this.id = id;
        this.importo = importo;
        this.timestamp = timestamp;
        this.motivo = motivo;
    }

    public int getId() {
        return id;
    }

    public double getImporto() {
        return importo;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getMotivo() {
        return motivo;
    }

    @Override
    public String toString() {
        String segno = importo >= 0 ? "+" : "";
        return timestamp.toString() + " | " + segno + String.format("%.2f", importo) + " | " + motivo;
    }
}
