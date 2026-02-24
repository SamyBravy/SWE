package it.unifi.ing.business.services;

import it.unifi.ing.dao.memory.InMemoryUserDao;
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
		authService = new AuthService(new InMemoryUserDao());
	}

	@Test
	void testSuccessfulAuthentication() {
		User dev = authService.register("Dev", "dev@test.com", "pass", "developer");
		assertNotNull(dev);
		assertTrue(dev instanceof Developer);

		User prov = authService.register("Prov", "prov@test.com", "pass", "modelprovider");
		assertNotNull(prov);
		assertTrue(prov instanceof ModelProvider);

		User sup = authService.register("Sup", "sup@test.com", "pass", "supervisor");
		assertNotNull(sup);
		assertTrue(sup instanceof Supervisor);

		User loggedInUser = authService.login("dev@test.com", "pass");
		assertNotNull(loggedInUser);
	}

	@Test
	void testAuthenticationErrors() {
		authService.register("ValidUser", "valid@test.com", "pass", "developer");

		User dup = authService.register("Duplicate", "valid@test.com", "pass", "developer");
		assertNull(dup);

		User invalidRole = authService.register("X", "x@test.com", "pass", "invalid_role");
		assertNull(invalidRole);

		assertNull(authService.login("valid@test.com", "wrong_password"));

		assertNull(authService.login("nobody@test.com", "pass"));
	}
}
