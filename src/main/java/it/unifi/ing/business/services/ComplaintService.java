package it.unifi.ing.business.services;

import it.unifi.ing.dao.interfaces.ComplaintDao;
import it.unifi.ing.domain.AiModel;
import it.unifi.ing.domain.Complaint;
import it.unifi.ing.domain.ComplaintStatus;
import it.unifi.ing.domain.ModelStatus;
import it.unifi.ing.domain.Developer;

import java.util.List;

/**
 * Service for complaint management.
 * Allows the Supervisor to accept or reject complaints,
 * with token refund and model blocking options.
 */
public class ComplaintService {

	private final ComplaintDao complaintDao;
	private int nextId;

	public ComplaintService(ComplaintDao complaintDao) {
		this.complaintDao = complaintDao;
		this.nextId = 1;
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
	 * Accepts a complaint, refunding tokens and optionally blocking the model
	 * permanently.
	 */
	public void acceptComplaint(Complaint complaint, int refundedTokens, boolean blockModel) {
		complaint.setStatus(ComplaintStatus.ACCEPTED);

		if (refundedTokens > 0) {
			double refund = refundedTokens * complaint.getModel().getCostPerToken();
			complaint.getDeveloper().getWallet().addFunds(refund,
					"REFUND complaint #" + complaint.getId() + ": " + refundedTokens + " tokens");
		}

		if (blockModel) {
			AiModel model = complaint.getModel();
			model.setStatus(ModelStatus.BLOCKED);
			System.out.println("🔒 Model '" + model.getName() + "' blocked.");
		}

		complaintDao.update(complaint);
	}

	/**
	 * Rejects a complaint with the provided reason.
	 */
	public void rejectComplaint(Complaint complaint, String reason) {
		complaint.setStatus(ComplaintStatus.REJECTED);
		complaint.setRejectionReasons(reason);
		complaintDao.update(complaint);
	}

	public void fileComplaint(Developer developer, AiModel model, String description,
			List<String> promptLogs) {
		Complaint complaint = Complaint.submit(nextId++, developer, model, description, promptLogs);
		complaintDao.save(complaint);
	}
}
