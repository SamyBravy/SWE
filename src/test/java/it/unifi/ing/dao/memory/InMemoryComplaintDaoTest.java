package it.unifi.ing.dao.memory;

import it.unifi.ing.domain.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryComplaintDaoTest {

    private InMemoryComplaintDao dao;
    private Developer developer;
    private AiModel model;

    @BeforeEach
    void setUp() {
        dao = new InMemoryComplaintDao();
        developer = new Developer(1, "Dev", "dev@test.com", "pass");
        ModelProvider prov = new ModelProvider(2, "Prov", "prov@test.com", "pass");
        model = AiModel.submitForReview(1, "Model", "Desc", 0.01, "s.bin", "c.json", prov);
    }

    @Test
    void testSaveAndFindById() {
        Complaint c = Complaint.submit(1, developer, model, "Issue", Arrays.asList("log1"));
        dao.save(c);
        assertNotNull(dao.findById(1));
        assertEquals(1, dao.findById(1).getId());
    }

    @Test
    void testFindAll() {
        dao.save(Complaint.submit(1, developer, model, "Issue1", Arrays.asList("log1")));
        dao.save(Complaint.submit(2, developer, model, "Issue2", Arrays.asList("log2")));
        assertEquals(2, dao.findAll().size());
    }

    @Test
    void testFindByStatus() {
        Complaint c1 = Complaint.submit(1, developer, model, "Issue1", Arrays.asList("log1"));
        Complaint c2 = Complaint.submit(2, developer, model, "Issue2", Arrays.asList("log2"));
        c2.setStatus(ComplaintStatus.ACCEPTED);
        dao.save(c1);
        dao.save(c2);

        assertEquals(1, dao.findByStatus(ComplaintStatus.PENDING_REVIEW).size());
        assertEquals(1, dao.findByStatus(ComplaintStatus.ACCEPTED).size());
        assertEquals(1, dao.findByStatus(ComplaintStatus.PENDING_REVIEW).get(0).getId());
    }

    @Test
    void testUpdate() {
        Complaint c = Complaint.submit(1, developer, model, "Issue", Arrays.asList("log1"));
        dao.save(c);
        c.setStatus(ComplaintStatus.REJECTED);
        dao.update(c);
        assertEquals(ComplaintStatus.REJECTED, dao.findById(1).getStatus());
    }

    @Test
    void testDelete() {
        dao.save(Complaint.submit(1, developer, model, "Issue", Arrays.asList("log1")));
        dao.delete(1);
        assertNull(dao.findById(1));
    }

    @Test
    void testFindByIdNonExistent() {
        assertNull(dao.findById(999));
    }
}
