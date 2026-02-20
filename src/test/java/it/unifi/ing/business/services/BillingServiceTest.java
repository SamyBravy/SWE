package it.unifi.ing.business.services;

import it.unifi.ing.dao.memory.InMemorySessionDao;
import it.unifi.ing.domain.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class BillingServiceTest {

	private BillingService billingService;
	private Developer developer;
	private AiModel model;
	private GPU gpu;

	@BeforeEach
	void setUp() {
		billingService = new BillingService(new StandardBillingStrategy());
		developer = new Developer(1, "Dev", "dev@test.com", "pass");
		developer.getWallet().addFunds(100.0);

		ModelProvider prov = new ModelProvider(2, "Prov", "prov@test.com", "pass");
		model = new AiModel(1, "TestModel", "Desc", 0.005, "s.bin", "c.json", prov);
		model.setCostPerTokenPlatform(0.005);
		gpu = new GPU(1);
	}

	@Test
	void testCalculateCost() {
		Session session = new Session(1, developer, model, gpu);
		session.addTokens(100);
		double cost = billingService.calculateCost(session);
		assertEquals(1.0, cost, 0.001);
	}

	@Test
	void testChargeCost() {
		Session session = new Session(1, developer, model, gpu);
		session.addTokens(100);
		double before = developer.getWallet().getBalance();
		boolean charged = billingService.chargeCost(session);
		assertTrue(charged);
		assertTrue(developer.getWallet().getBalance() < before);
	}

	@Test
	void testCalculateCostZeroTokens() {
		Session session = new Session(1, developer, model, gpu);
		assertEquals(0.0, billingService.calculateCost(session));
	}

	@Test
	void testChangeBillingStrategy() {
		billingService.setBillingStrategy(new PremiumDiscountStrategy(0.20));
		Session session = new Session(1, developer, model, gpu);
		session.addTokens(100);
		double cost = billingService.calculateCost(session);
		assertEquals(0.8, cost, 0.001);
	}

	@Test
	void testChargeCostInsufficientFunds() {
		Developer poorDev = new Developer(3, "Poor", "poor@test.com", "pass");
		poorDev.getWallet().addFunds(0.001);
		Session session = new Session(1, poorDev, model, gpu);
		session.addTokens(1000);
		boolean charged = billingService.chargeCost(session);
		assertFalse(charged);
	}

	@Test
	void testBillActiveSessions() {
		InMemorySessionDao sessionDao = new InMemorySessionDao();
		BillingService bs = new BillingService(new StandardBillingStrategy(), sessionDao);

		Session session = new Session(1, developer, model, gpu);
		session.addTokens(100);
		sessionDao.save(session);

		double before = developer.getWallet().getBalance();
		bs.billActiveSessions();
		assertTrue(developer.getWallet().getBalance() < before);
	}

	@Test
	void testChargeCostZeroTokensNoCharge() {
		Session session = new Session(1, developer, model, gpu);
		double before = developer.getWallet().getBalance();
		billingService.chargeCost(session);
		assertEquals(before, developer.getWallet().getBalance());
	}
}
