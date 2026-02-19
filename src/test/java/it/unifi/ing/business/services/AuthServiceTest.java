package it.unifi.ing.business.services;

import it.unifi.ing.dao.memory.InMemoryUtenteDAO;
import it.unifi.ing.domain.Developer;
import it.unifi.ing.domain.ModelProvider;
import it.unifi.ing.domain.Supervisor;
import it.unifi.ing.domain.Utente;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest {

	private AuthService authService;

	@BeforeEach
	void setUp() {
		authService = new AuthService(new InMemoryUtenteDAO());
	}

	@Test
	void testRegistraDeveloper() {
		Utente utente = authService.registra("Mario", "mario@test.com", "pass123", "developer");
		assertNotNull(utente);
		assertInstanceOf(Developer.class, utente);
		assertEquals("Mario", utente.getNome());
	}

	@Test
	void testRegistraModelProvider() {
		Utente utente = authService.registra("Luca", "luca@test.com", "pass456", "modelprovider");
		assertNotNull(utente);
		assertInstanceOf(ModelProvider.class, utente);
	}

	@Test
	void testRegistraSupervisor() {
		Utente utente = authService.registra("Anna", "anna@test.com", "pass789", "supervisor");
		assertNotNull(utente);
		assertInstanceOf(Supervisor.class, utente);
	}

	@Test
	void testRegistraEmailDuplicata() {
		authService.registra("Mario", "mario@test.com", "pass123", "developer");
		Utente duplicato = authService.registra("Altro", "mario@test.com", "pass456", "developer");
		assertNull(duplicato);
	}

	@Test
	void testRegistraRuoloInvalido() {
		Utente utente = authService.registra("Mario", "mario@test.com", "pass123", "admin");
		assertNull(utente);
	}

	@Test
	void testLoginSuccesso() {
		authService.registra("Mario", "mario@test.com", "pass123", "developer");
		Utente utente = authService.login("mario@test.com", "pass123");
		assertNotNull(utente);
		assertEquals("Mario", utente.getNome());
	}

	@Test
	void testLoginPasswordErrata() {
		authService.registra("Mario", "mario@test.com", "pass123", "developer");
		Utente utente = authService.login("mario@test.com", "wrongpass");
		assertNull(utente);
	}

	@Test
	void testLoginEmailNonEsistente() {
		Utente utente = authService.login("nope@test.com", "pass");
		assertNull(utente);
	}
}
