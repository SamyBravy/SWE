package it.unifi.ing.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Wallet: manages a developer's credit balance and transaction history.
 * UML: id, balance, transactionHistory
 */
public class Wallet {

    private String id;
    private double balance;
    private final List<Transaction> transactionHistory;
    private int nextTransactionId;

    public Wallet() {
        this.id = java.util.UUID.randomUUID().toString();
        this.balance = 0.0;
        this.transactionHistory = new ArrayList<>();
        this.nextTransactionId = 1;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getBalance() {
        return balance;
    }

    /**
     * Adds funds to the wallet.
     * UML: addFunds(amount)
     */
    public void addFunds(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        this.balance += amount;
        transactionHistory.add(new Transaction(
                nextTransactionId++, amount, LocalDateTime.now(),
                "TOP-UP: +" + String.format("%.2f", amount)));
    }

    /**
     * Charges the wallet.
     * UML: charge(amount)
     * @return true if the charge was successful
     */
    public boolean charge(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (amount > balance) {
            return false;
        }
        this.balance -= amount;
        transactionHistory.add(new Transaction(
                nextTransactionId++, -amount, LocalDateTime.now(),
                "CHARGE: -" + String.format("%.2f", amount)));
        return true;
    }

    /**
     * Adds credit with a specific reason (e.g. refund).
     */
    public void addFundsWithReason(double amount, String reason) {
        this.balance += amount;
        transactionHistory.add(new Transaction(
                nextTransactionId++, amount, LocalDateTime.now(), reason));
    }

    public List<Transaction> getTransactionHistory() {
        return Collections.unmodifiableList(transactionHistory);
    }

    @Override
    public String toString() {
        return "Wallet [id=" + id + ", balance=" + String.format("%.2f", balance) + "]";
    }
}
