package it.unifi.ing.business.services;

import it.unifi.ing.domain.Complaint;
import it.unifi.ing.domain.Developer;
import it.unifi.ing.domain.Session;
import it.unifi.ing.domain.Transaction;

import java.util.List;

public class StatsService {

	private final SessionService sessionService;
	private final ComplaintService complaintService;

	public StatsService(SessionService sessionService, ComplaintService complaintService) {
		this.sessionService = sessionService;
		this.complaintService = complaintService;
	}

	public DeveloperStats getDeveloperStats(Developer developer) {
		List<Session> sessions = sessionService.findByUser(developer.getId());
		int totalSessions = sessions.size();
		int activeSessions = (int) sessions.stream().filter(Session::isActive).count();
		int totalTokens = sessions.stream().mapToInt(Session::getTotalTokensUsed).sum();

		List<Complaint> devComplaints = complaintService.findByDeveloper(developer.getId());
		List<Transaction> transactions = developer.getWallet().getTransactionHistory();

		return new DeveloperStats(totalSessions, activeSessions, totalTokens, sessions, devComplaints, transactions);
	}

	public record DeveloperStats(
			int totalSessions,
			int activeSessions,
			int totalTokens,
			List<Session> sessions,
			List<Complaint> complaints,
			List<Transaction> transactions
	) {}
}
