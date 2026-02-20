package it.unifi.ing.business.services;

import it.unifi.ing.dao.memory.InMemoryUserDAO;
import it.unifi.ing.domain.Developer;
import it.unifi.ing.domain.ModelProvider;
import it.unifi.ing.domain.Supervisor;
import it.unifi.ing.domain.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest {

	private AuthService authService;

	@BeforeEach
	void setUp() {
		authService = new AuthService(new InMemoryUserDAO());
	}

	@Test
	void testRegisterDeveloper() {
		User user = authService.register("Dev", "dev@test.com", "pass", "developer");
		assertNotNull(user);
		assertTrue(user instanceof Developer);
	}

	@Test
	void testRegisterModelProvider() {
		User user = authService.register("Prov", "prov@test.com", "pass", "modelprovider");
		assertNotNull(user);
		assertTrue(user instanceof ModelProvider);
	}

	@Test
	void testRegisterSupervisor() {
		User user = authService.register("Sup", "sup@test.com", "pass", "supervisor");
		assertNotNull(user);
		assertTrue(user instanceof Supervisor);
	}

	@Test
	void testRegisterDuplicateEmail() {
		authService.register("Dev1", "dev@test.com", "pass", "developer");
		User dup = authService.register("Dev2", "dev@test.com", "pass", "developer");
		assertNull(dup);
	}

	@Test
	void testRegisterInvalidRole() {
		assertNull(authService.register("X", "x@test.com", "pass", "invalid"));
	}

	@Test
	void testLoginSuccess() {
		authService.register("Dev", "dev@test.com", "pass", "developer");
		User user = authService.login("dev@test.com", "pass");
		assertNotNull(user);
	}

	@Test
	void testLoginWrongPassword() {
		authService.register("Dev", "dev@test.com", "pass", "developer");
		assertNull(authService.login("dev@test.com", "wrong"));
	}

	@Test
	void testLoginNonExistent() {
		assertNull(authService.login("nobody@test.com", "pass"));
	}
}
