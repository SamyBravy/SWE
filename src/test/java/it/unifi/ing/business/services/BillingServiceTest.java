package it.unifi.ing.business.services;

import it.unifi.ing.domain.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

class BillingServiceTest {

	private ModelProvider provider;
	private Developer developer;
	private Modello modello;
	private GPU gpu;

	@BeforeEach
	void setUp() {
		provider = new ModelProvider(1, "Provider", "prov@test.com", "pass");
		developer = new Developer(2, "Dev", "dev@test.com", "pass");
		modello = new Modello(1, "TestModel", "Desc", 0.005, "s.bin", "c.json", provider);
		modello.setCostoPerTokenPiattaforma(0.005);
		// getCostoTotalePerToken() = 0.005 + 0.005 = 0.01
		gpu = new GPU(1);
	}

	@Test
	void testStandardBillingStrategy() {
		BillingService billingService = new BillingService(new StandardBillingStrategy());
		Sessione sessione = new Sessione(1, developer, modello, gpu);
		sessione.addTokens(100);

		double costo = billingService.calcolaCosto(sessione);
		assertEquals(1.0, costo, 0.001); // 100 * 0.01
	}

	@Test
	void testPremiumDiscountStrategy() {
		BillingService billingService = new BillingService(new PremiumDiscountStrategy(0.20));
		Sessione sessione = new Sessione(1, developer, modello, gpu);
		sessione.addTokens(100);

		double costo = billingService.calcolaCosto(sessione);
		assertEquals(0.80, costo, 0.001); // 100 * 0.01 * 0.80
	}

	@Test
	void testAddebitaCostoSuccesso() {
		developer.getWallet().addCredito(10.0);
		BillingService billingService = new BillingService(new StandardBillingStrategy());
		Sessione sessione = new Sessione(1, developer, modello, gpu);
		sessione.addTokens(100);

		assertTrue(billingService.addebitaCosto(sessione));
		assertEquals(9.0, developer.getWallet().getSaldo(), 0.001);
	}

	@Test
	void testAddebitaCostoInsufficiente() {
		// Saldo è 0
		BillingService billingService = new BillingService(new StandardBillingStrategy());
		Sessione sessione = new Sessione(1, developer, modello, gpu);
		sessione.addTokens(100);

		assertFalse(billingService.addebitaCosto(sessione));
	}

	@Test
	void testCambioStrategia() {
		BillingService billingService = new BillingService(new StandardBillingStrategy());
		Sessione sessione = new Sessione(1, developer, modello, gpu);
		sessione.addTokens(100);

		double costoStandard = billingService.calcolaCosto(sessione);

		billingService.setBillingStrategy(new PremiumDiscountStrategy(0.50));
		double costoPremium = billingService.calcolaCosto(sessione);

		assertTrue(costoPremium < costoStandard);
		assertEquals(0.50, costoPremium, 0.001); // 100 * 0.01 * 0.50
	}

	@Test
	void testPremiumDiscountInvalido() {
		assertThrows(IllegalArgumentException.class, () -> new PremiumDiscountStrategy(-0.1));
		assertThrows(IllegalArgumentException.class, () -> new PremiumDiscountStrategy(1.5));
	}

	@Test
	void testCostoConZeroToken() {
		BillingService billingService = new BillingService(new StandardBillingStrategy());
		Sessione sessione = new Sessione(1, developer, modello, gpu);
		// 0 tokens

		double costo = billingService.calcolaCosto(sessione);
		assertEquals(0.0, costo);
	}
}
