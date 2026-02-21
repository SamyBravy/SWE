package it.unifi.ing.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Wallet {

	private int id;
	private double balance;
	private final List<Transaction> transactionHistory;
	private int nextTransactionId;

	public Wallet(int id) {
		this.id = id;
		this.balance = 0.0;
		this.transactionHistory = new ArrayList<>();
		this.nextTransactionId = 1;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public double getBalance() {
		return balance;
	}

	public void addFunds(double amount, String... optionalReason) {
		//item 23: Check parameters for validity
		if (amount <= 0) {
			throw new IllegalArgumentException("Amount must be positive");
		}

		String reason = (optionalReason != null && optionalReason.length > 0)
				? optionalReason[0]
				: "TOP-UP"; //if optionalReason is null then reason is "TOP-UP"

		this.balance += amount;
		transactionHistory.add(Transaction.create(
				nextTransactionId++, amount, LocalDateTime.now(), reason));
	}

	public boolean charge(double amount) {
		if (amount < 0) {
			throw new IllegalArgumentException("Amount cannot be negative");
		}
		if (amount == 0) {
			return true;
		}
		if (amount > balance) {
			return false;
		}
		this.balance -= amount;
		transactionHistory.add(Transaction.create(
				nextTransactionId++, -amount, LocalDateTime.now(),
				"CHARGE: -" + String.format("%.2f", amount)));
		return true;
	}

	public List<Transaction> getTransactionHistory() {
		return Collections.unmodifiableList(transactionHistory);
	}

	@Override
	public String toString() {
		return "Wallet [id=" + id + ", balance=" + String.format("%.2f", balance) + "]";
	}
}
