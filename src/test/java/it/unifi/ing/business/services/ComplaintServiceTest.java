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
		model = AiModel.submitForReview(1, "TestModel", "Desc", 0.01, "s.bin", "c.json", prov);
		model.setCostPerTokenPlatform(0.005);
	}

	@Test
	void testSaveAndFindComplaint() {
		complaintService.fileComplaint(developer, model, "Issue", Arrays.asList("log1"));
		assertNotNull(complaintService.findById(1));
	}

	@Test
	void testAcceptComplaint() {
		complaintService.fileComplaint(developer, model, "Issue", Arrays.asList("log1"));
		Complaint c = complaintService.findById(1);
		double balanceBefore = developer.getWallet().getBalance();
		complaintService.acceptComplaint(c, 100, false);
		assertEquals(ComplaintStatus.ACCEPTED, c.getStatus());
		assertTrue(developer.getWallet().getBalance() > balanceBefore);
	}

	@Test
	void testRejectComplaint() {
		complaintService.fileComplaint(developer, model, "Issue", Arrays.asList("log1"));
		Complaint c = complaintService.findById(1);
		complaintService.rejectComplaint(c, "Invalid");
		assertEquals(ComplaintStatus.REJECTED, c.getStatus());
		assertEquals("Invalid", c.getRejectionReasons());
	}

	@Test
	void testGetPendingComplaints() {
		complaintService.fileComplaint(developer, model, "Issue1", Arrays.asList("l1"));
		complaintService.fileComplaint(developer, model, "Issue2", Arrays.asList("l2"));
		assertEquals(2, complaintService.getPendingComplaints().size());
	}

	@Test
	void testAcceptComplaintWithModelBlock() {
		complaintService.fileComplaint(developer, model, "Issue", Arrays.asList("log1"));
		Complaint c = complaintService.findById(1);
		complaintService.acceptComplaint(c, 0, true);
		assertEquals(ModelStatus.BLOCKED, model.getStatus());
	}
}
