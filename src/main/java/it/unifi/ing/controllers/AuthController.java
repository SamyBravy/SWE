package it.unifi.ing.controllers;

import it.unifi.ing.business.services.AuthService;
import it.unifi.ing.domain.User;

import java.util.Scanner;

public class AuthController {

	private final AuthService authService;
	private final Scanner scanner;

	public AuthController(AuthService authService, Scanner scanner) {
		this.authService = authService;
		this.scanner = scanner;
	}

	public User showMenu() {
		while (true) {
			System.out.println("\n╔══════════════════════════════════════╗");
			System.out.println("║   GPU CLUSTER MANAGEMENT - Welcome   ║");
			System.out.println("╠══════════════════════════════════════╣");
			System.out.println("║  1. Login                            ║");
			System.out.println("║  2. Register                         ║");
			System.out.println("║  0. Exit                             ║");
			System.out.println("╚══════════════════════════════════════╝");
			System.out.print("Choice: ");

			String choice = scanner.nextLine().trim();

			switch (choice) {
				case "1" -> {
					User loginUser = handleLogin();
					if (loginUser != null)
						return loginUser;
				}
				case "2" -> {
					User regUser = handleRegistration();
					if (regUser != null)
						return regUser;
				}
				case "0" -> {
					System.out.println("Goodbye!");
					System.exit(0);
				}
				default -> System.out.println("Invalid choice.");
			}
		}
	}

	private User handleLogin() {
		System.out.println("\n--- LOGIN ---");
		System.out.print("Email: ");
		String email = scanner.nextLine().trim();
		System.out.print("Password: ");
		String password = scanner.nextLine().trim();

		User user = authService.login(email, password);
		if (user != null) {
			System.out.println("✅ Login successful! Welcome, " + user.getName()
					+ " (" + user.getRole() + ")");
			return user;
		} else {
			System.out.println("❌ Invalid credentials.");
			return null;
		}
	}

	private User handleRegistration() {
		System.out.println("\n--- REGISTRATION ---");
		System.out.print("Name: ");
		String name = scanner.nextLine().trim();
		System.out.print("Email: ");
		String email = scanner.nextLine().trim();
		System.out.print("Password: ");
		String password = scanner.nextLine().trim();

		System.out.println("Select role:");
		System.out.println("  1. Developer");
		System.out.println("  2. ModelProvider");
		System.out.println("  3. Supervisor");
		System.out.print("Choice: ");
		String roleChoice = scanner.nextLine().trim();

		String role;
		switch (roleChoice) {
			case "1":
				role = "developer";
				break;
			case "2":
				role = "modelprovider";
				break;
			case "3":
				role = "supervisor";
				break;
			default:
				System.out.println("Invalid role.");
				return null;
		}

		User user = authService.register(name, email, password, role);
		if (user != null) {
			System.out.println("✅ Registration complete! Welcome, " + user.getName()
					+ " (" + user.getRole() + ")");
			return user;
		} else {
			System.out.println("❌ Email already registered or invalid role.");
			return null;
		}
	}
}
