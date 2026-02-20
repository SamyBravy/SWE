package it.unifi.ing.business.services;

import it.unifi.ing.dao.interfaces.ComplaintDao;
import it.unifi.ing.dao.interfaces.UserDao;
import it.unifi.ing.domain.AiModel;
import it.unifi.ing.domain.Complaint;
import it.unifi.ing.domain.ComplaintStatus;
import it.unifi.ing.domain.ModelStatus;

import java.util.List;

/**
 * Service for complaint management.
 * Allows the Supervisor to accept or reject complaints,
 * with token refund and model blocking options.
 */
public class ComplaintService {

    private final ComplaintDao complaintDao;
    private final UserDao userDao;

    public ComplaintService(ComplaintDao complaintDao, UserDao userDao) {
        this.complaintDao = complaintDao;
        this.userDao = userDao;
    }

    public List<Complaint> getPendingComplaints() {
        return complaintDao.findByStatus(ComplaintStatus.PENDING_REVIEW);
    }

    public List<Complaint> getAllComplaints() {
        return complaintDao.findAll();
    }

    public Complaint findById(int id) {
        return complaintDao.findById(id);
    }

    /**
     * Accepts a complaint, refunding tokens and optionally blocking the model.
     */
    public void acceptComplaint(Complaint complaint, int refundedTokens, double blockHours) {
        complaint.setStatus(ComplaintStatus.ACCEPTED);

        if (refundedTokens > 0) {
            double refund = refundedTokens * complaint.getModel().getCostPerToken();
            complaint.getDeveloper().getWallet().addFundsWithReason(refund,
                    "REFUND complaint #" + complaint.getId() + ": " + refundedTokens + " tokens");
        }

        if (blockHours > 0) {
            AiModel model = complaint.getModel();
            model.setStatus(ModelStatus.BLOCKED);
            System.out.println("🔒 Model '" + model.getName() + "' blocked for " + blockHours + " hours.");
        }

        complaintDao.update(complaint);
    }

    /**
     * Rejects a complaint with the provided reasons.
     */
    public void rejectComplaint(Complaint complaint, String reasons) {
        complaint.setStatus(ComplaintStatus.REJECTED);
        complaint.setRejectionReasons(reasons);
        complaintDao.update(complaint);
    }

    public void saveComplaint(Complaint complaint) {
        complaintDao.save(complaint);
    }
}
