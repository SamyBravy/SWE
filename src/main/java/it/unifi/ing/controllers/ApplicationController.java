package it.unifi.ing.controllers;

import it.unifi.ing.domain.Developer;
import it.unifi.ing.domain.ModelProvider;
import it.unifi.ing.domain.Supervisor;
import it.unifi.ing.domain.User;

/**
 * Main dispatcher controller that handles the root application loop.
 */
public class ApplicationController {

	private final AuthController authController;
	private final DeveloperController developerController;
	private final ModelProviderController modelProviderController;
	private final SupervisorController supervisorController;

	public ApplicationController(AuthController authController,
			DeveloperController developerController,
			ModelProviderController modelProviderController,
			SupervisorController supervisorController) {
		this.authController = authController;
		this.developerController = developerController;
		this.modelProviderController = modelProviderController;
		this.supervisorController = supervisorController;
	}

	public void start() {
		while (true) {
			User user = authController.showMenu();

			if (user instanceof Developer dev) {
				developerController.showMenu(dev);
			} else if (user instanceof ModelProvider provider) {
				modelProviderController.showMenu(provider);
			} else if (user instanceof Supervisor supervisor) {
				supervisorController.showMenu(supervisor);
			}
		}
	}
}
