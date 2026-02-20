package it.unifi.ing.domain;

import java.time.LocalDateTime;

/**
 * Transaction: records a single financial transaction in a Wallet.
 * UML: id, amount, timestamp, reason
 */
public class Transaction {

    private final int id;
    private final double amount;
    private final LocalDateTime timestamp;
    private final String reason;

    public Transaction(int id, double amount, LocalDateTime timestamp, String reason) {
        this.id = id;
        this.amount = amount;
        this.timestamp = timestamp;
        this.reason = reason;
    }

    public int getId() {
        return id;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getReason() {
        return reason;
    }

    @Override
    public String toString() {
        String sign = amount >= 0 ? "+" : "";
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return timestamp.format(formatter) + " | " + sign + String.format("%.2f", amount) + " | " + reason;
    }
}
