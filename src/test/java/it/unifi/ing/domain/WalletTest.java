package it.unifi.ing.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

class WalletTest {

	private Wallet wallet;

	@BeforeEach
	void setUp() {
		wallet = new Wallet();
	}

	@Test
	void testSaldoIniziale() {
		assertEquals(0.0, wallet.getSaldo());
	}

	@Test
	void testAddCredito() {
		wallet.addCredito(100.0);
		assertEquals(100.0, wallet.getSaldo());
	}

	@Test
	void testAddCreditoMultiplo() {
		wallet.addCredito(50.0);
		wallet.addCredito(30.0);
		assertEquals(80.0, wallet.getSaldo());
	}

	@Test
	void testAddCreditoNegativo() {
		assertThrows(IllegalArgumentException.class, () -> wallet.addCredito(-10.0));
	}

	@Test
	void testDeduciCredito() {
		wallet.addCredito(100.0);
		assertTrue(wallet.deduciCredito(40.0));
		assertEquals(60.0, wallet.getSaldo(), 0.001);
	}

	@Test
	void testDeduciCreditoInsufficiente() {
		wallet.addCredito(10.0);
		assertFalse(wallet.deduciCredito(50.0));
		assertEquals(10.0, wallet.getSaldo());
	}

	@Test
	void testDeduciCreditoNegativo() {
		assertThrows(IllegalArgumentException.class, () -> wallet.deduciCredito(-5.0));
	}

	@Test
	void testStoricoTransazioni() {
		wallet.addCredito(100.0);
		wallet.deduciCredito(30.0);
		assertEquals(2, wallet.getStoricoTransazioni().size());
		assertTrue(wallet.getStoricoTransazioni().get(0).getMotivo().contains("RICARICA"));
		assertTrue(wallet.getStoricoTransazioni().get(1).getMotivo().contains("ADDEBITO"));
	}

	@Test
	void testStoricoTransazioniImmutabile() {
		wallet.addCredito(50.0);
		assertThrows(UnsupportedOperationException.class,
				() -> wallet.getStoricoTransazioni().add(
						new Transaction(99, 0, java.time.LocalDateTime.now(), "test")));
	}
}
