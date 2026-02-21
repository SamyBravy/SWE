package it.unifi.ing.controllers;

import it.unifi.ing.domain.Developer;

import java.util.Scanner;

/**
 * Controller for wallet/credit operations.
 */
public class WalletController {

	private final Scanner scanner;

	public WalletController(Scanner scanner) {
		this.scanner = scanner;
	}

	public void topUpCredit(Developer developer) {
		System.out.println("\n--- TOP-UP CREDIT ---");
		System.out.println("Current balance: €" + String.format("%.2f", developer.getWallet().getBalance()));
		System.out.print("Amount to add (€): ");

		try {
			double amount = Double.parseDouble(scanner.nextLine().trim());
			if (amount <= 0) {
				System.out.println("Amount must be positive.");
				return;
			}
			developer.getWallet().addFunds(amount);
			System.out.println("Top-up of €" + String.format("%.2f", amount) + " successful!");
			System.out.println("   New balance: €" + String.format("%.2f", developer.getWallet().getBalance()));
		} catch (NumberFormatException e) {
			System.out.println("Invalid amount.");
		}
	}
}
