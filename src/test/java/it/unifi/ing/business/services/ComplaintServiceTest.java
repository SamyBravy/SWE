package it.unifi.ing.business.services;

import it.unifi.ing.dao.memory.InMemoryComplaintDAO;
import it.unifi.ing.dao.memory.InMemoryUserDAO;
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
        InMemoryComplaintDAO complaintDao = new InMemoryComplaintDAO();
        InMemoryUserDAO userDao = new InMemoryUserDAO();
        complaintService = new ComplaintService(complaintDao, userDao);

        developer = new Developer(1, "Dev", "dev@test.com", "pass");
        developer.getWallet().addFunds(100.0);
        userDao.save(developer);

        ModelProvider prov = new ModelProvider(2, "Prov", "prov@test.com", "pass");
        model = new AiModel(1, "TestModel", "Desc", 0.01, "s.bin", "c.json", prov);
        model.setCostPerTokenPlatform(0.005);
    }

    @Test
    void testSaveAndFindComplaint() {
        Complaint c = new Complaint(1, developer, model, "Issue", Arrays.asList("log1"));
        complaintService.saveComplaint(c);
        assertNotNull(complaintService.findById(1));
    }

    @Test
    void testAcceptComplaint() {
        Complaint c = new Complaint(1, developer, model, "Issue", Arrays.asList("log1"));
        complaintService.saveComplaint(c);
        double balanceBefore = developer.getWallet().getBalance();
        complaintService.acceptComplaint(c, 100, 0);
        assertEquals(ComplaintStatus.ACCEPTED, c.getStatus());
        assertTrue(developer.getWallet().getBalance() > balanceBefore);
    }

    @Test
    void testRejectComplaint() {
        Complaint c = new Complaint(1, developer, model, "Issue", Arrays.asList("log1"));
        complaintService.saveComplaint(c);
        complaintService.rejectComplaint(c, "Invalid");
        assertEquals(ComplaintStatus.REJECTED, c.getStatus());
        assertEquals("Invalid", c.getRejectionReasons());
    }

    @Test
    void testGetPendingComplaints() {
        complaintService.saveComplaint(new Complaint(1, developer, model, "Issue1", Arrays.asList("l1")));
        complaintService.saveComplaint(new Complaint(2, developer, model, "Issue2", Arrays.asList("l2")));
        assertEquals(2, complaintService.getPendingComplaints().size());
    }

    @Test
    void testAcceptComplaintWithModelBlock() {
        Complaint c = new Complaint(1, developer, model, "Issue", Arrays.asList("log1"));
        complaintService.saveComplaint(c);
        complaintService.acceptComplaint(c, 0, 24);
        assertEquals(ModelStatus.BLOCKED, model.getStatus());
    }
}
