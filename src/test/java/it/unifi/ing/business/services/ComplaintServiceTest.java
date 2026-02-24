package it.unifi.ing.business.services;

import it.unifi.ing.dao.memory.InMemoryComplaintDao;
import it.unifi.ing.dao.memory.InMemoryUserDao;
import it.unifi.ing.domain.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.*;

class ComplaintServiceTest {

	private ComplaintService complaintService;
	private Developer developer;
	private AiModel model;

	@BeforeEach
	void setUp() {
		InMemoryComplaintDao complaintDao = new InMemoryComplaintDao();
		complaintService = new ComplaintService(complaintDao);

		developer = new Developer(1, "Dev", "dev@test.com", "pass");
		developer.getWallet().addFunds(100.0);

		ModelProvider prov = new ModelProvider(2, "Prov", "prov@test.com", "pass");
		model = AiModel.submitForReview(1, "TestModel", "Desc", 0.01, "s.safetensors", "c.json", prov);
		model.setCostPerTokenPlatform(0.005);
	}

	@Test
	void testComplaintAcceptance() {
		complaintService.fileComplaint(developer, model, "Issue1", Arrays.asList("l1"));
		complaintService.fileComplaint(developer, model, "Issue2", Arrays.asList("l2"));
		assertEquals(2, complaintService.getPendingComplaints().size());

		Complaint c = complaintService.findById(1);
		assertNotNull(c);

		double balanceBefore = developer.getWallet().getBalance();
		complaintService.acceptComplaint(c, 100, true);

		assertEquals(ComplaintStatus.ACCEPTED, c.getStatus());
		assertTrue(developer.getWallet().getBalance() > balanceBefore);
		assertEquals(ModelStatus.BLOCKED, model.getStatus());
	}

	@Test
	void testComplaintRejection() {
		complaintService.fileComplaint(developer, model, "Fake issue", Arrays.asList("log1"));
		Complaint c = complaintService.findById(1);

		complaintService.rejectComplaint(c, "Invalid and unfounded");
		assertEquals(ComplaintStatus.REJECTED, c.getStatus());
		assertEquals("Invalid and unfounded", c.getRejectionReasons());
	}
}
