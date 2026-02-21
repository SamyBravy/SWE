package it.unifi.ing.domain;

import java.time.LocalDateTime;
import java.util.Objects;

public final class Transaction {

    private final int id;
    private final double amount;
    private final LocalDateTime timestamp;
    private final String reason;

    private Transaction(int id, double amount, LocalDateTime timestamp, String reason) {
        this.id = id;
        this.amount = amount;
        this.timestamp = timestamp;
        this.reason = reason;
    }

    //item: consider providing static factory methods instead of constructors
    public static Transaction create(int id, double amount, LocalDateTime timestamp, String reason) {
        if (id <= 0) {
            throw new IllegalArgumentException("Transaction ID must be positive. Received: " + id);
        }

        if (Double.isNaN(amount) || Double.isInfinite(amount)) {
            throw new IllegalArgumentException("Invalid amount: " + amount);
        }

        Objects.requireNonNull(timestamp, "Timestamp cannot be null");

        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("Transaction reason cannot be null or empty");
        }

        return new Transaction(id, amount, timestamp, reason);
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
