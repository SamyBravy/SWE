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
		model = AiModel.submitForReview(1, "TestModel", "Desc", 0.005, "s.bin", "c.json", prov);
		model.setCostPerTokenPlatform(0.005);
		gpu = new GPU(1);
	}

	@Test
	void testCalculateCost() {
		Session session = new Session(1, developer, model, gpu);
		double cost = billingService.calculateCost(session, 100);
		assertEquals(1.0, cost, 0.001);
	}

	@Test
	void testCalculateCostZeroTokens() {
		Session session = new Session(1, developer, model, gpu);
		assertEquals(0.0, billingService.calculateCost(session, 0));
	}

	@Test
	void testChangeBillingStrategy() {
		billingService.setBillingStrategy(new PremiumDiscountStrategy(0.20));
		Session session = new Session(1, developer, model, gpu);
		double cost = billingService.calculateCost(session, 100);
		assertEquals(0.8, cost, 0.001);
	}

}
