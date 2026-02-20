package it.unifi.ing.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.*;

class ComplaintTest {

	private Complaint complaint;
	private Developer developer;
	private AiModel model;

	@BeforeEach
	void setUp() {
		developer = new Developer(1, "Dev", "dev@test.com", "pass");
		ModelProvider provider = new ModelProvider(2, "Prov", "prov@test.com", "pass");
		model = AiModel.submitForReview(1, "Model", "Desc", 0.01, "s.bin", "c.json", provider);
		complaint = Complaint.submit(1, developer, model, "Bad responses", Arrays.asList("log1", "log2"));
	}

	@Test
	void testComplaintCreation() {
		assertEquals(1, complaint.getId());
		assertEquals(developer, complaint.getDeveloper());
		assertEquals(model, complaint.getModel());
		assertEquals(ComplaintStatus.PENDING_REVIEW, complaint.getStatus());
		assertEquals(2, complaint.getPromptLogs().size());
	}

	@Test
	void testStatusChange() {
		complaint.setStatus(ComplaintStatus.ACCEPTED);
		assertEquals(ComplaintStatus.ACCEPTED, complaint.getStatus());
	}

	@Test
	void testRejectionReasons() {
		complaint.setRejectionReasons("Not a valid complaint");
		assertEquals("Not a valid complaint", complaint.getRejectionReasons());
	}
}
