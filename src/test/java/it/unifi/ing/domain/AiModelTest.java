package it.unifi.ing.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class AiModelTest {

	private AiModel model;

	@BeforeEach
	void setUp() {
		ModelProvider provider = new ModelProvider(1, "TestProvider", "prov@test.com", "pass");
		model = AiModel.submitForReview(1, "GPT-Test", "Test model", 0.005, "model.safetensors", "config.json", provider);
	}

	@Test
	void testModelCreation() {
		assertEquals(1, model.getId());
		assertEquals("GPT-Test", model.getName());
		assertEquals(ModelStatus.PENDING_REVIEW, model.getStatus());
		assertEquals(0.005, model.getCostPerTokenProvider());
	}

	@Test
	void testCostPerToken() {
		model.setCostPerTokenPlatform(0.003);
		assertEquals(0.008, model.getCostPerToken(), 0.0001);
	}

	@Test
	void testStatusChange() {
		model.setStatus(ModelStatus.APPROVED);
		assertEquals(ModelStatus.APPROVED, model.getStatus());
	}
}
