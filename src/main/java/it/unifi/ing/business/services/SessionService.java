package it.unifi.ing.business.services;

import it.unifi.ing.dao.interfaces.SessionDao;
import it.unifi.ing.domain.AiModel;
import it.unifi.ing.domain.Developer;
import it.unifi.ing.domain.GPU;
import it.unifi.ing.domain.GpuCluster;
import it.unifi.ing.domain.GpuStatus;
import it.unifi.ing.domain.Session;

/**
 * Service for managing AI chat sessions.
 */
public class SessionService {

	private final SessionDao sessionDao;
	private final GpuCluster cluster;
	private final BillingService billingService;
	private int nextId;

	public SessionService(SessionDao sessionDao, GpuCluster cluster, BillingService billingService) {
		this.sessionDao = sessionDao;
		this.cluster = cluster;
		this.billingService = billingService;
		this.nextId = 1;
	}

	public Session openSession(Developer developer, AiModel model) {
		GPU gpu = cluster.getAvailableGpu();
		if (gpu == null) {
			return null;
		}

		gpu.setLoadedModel(model);
		Session session = new Session(nextId++, developer, model, gpu);
		sessionDao.save(session);
		return session;
	}

	public String sendPrompt(Session session, String prompt) {
		if (!session.isActive()) {
			return "Error: session has already ended.";
		}

		if (session.getGpu().getStatus() == GpuStatus.IDLE) {
			return "Error: assigned GPU is overheated. Session will be terminated.";
		}

		int tokensConsumed = Math.max(5, (int) Math.ceil(prompt.length()));

		double promptCost = tokensConsumed * session.getModel().getCostPerToken();
		double availableBalance = session.getDeveloper().getWallet().getBalance();
		if (promptCost > availableBalance) {
			closeSession(session);
			return "❌ Insufficient credit (need €" + String.format("%.2f", promptCost)
					+ ", available €" + String.format("%.2f", availableBalance)
					+ "). Session terminated automatically.";
		}

		boolean charged = session.getDeveloper().getWallet().charge(promptCost);
		if (!charged) {
			return "❌ Error during charge. Session terminated.";
		}

		session.addTokens(tokensConsumed);
		session.addTotalCost(promptCost);
		sessionDao.update(session);

		String response = session.getModel().generateResponse(prompt)
				+ " — (tokens used: " + tokensConsumed + ", cost: €" + String.format("%.4f", promptCost)
				+ ", balance: €" + String.format("%.2f", session.getDeveloper().getWallet().getBalance()) + ")";

		session.addLog("You: " + prompt);
		session.addLog("AI: " + response);

		return response;
	}

	public double closeSession(Session session) {
		if (!session.isActive()) {
			return 0;
		}

		double totalCost = billingService.calculateCost(session);

		session.close();
		sessionDao.update(session);
		cluster.releaseGpu(session.getGpu());

		return totalCost;
	}

	public Session findById(int id) {
		return sessionDao.findById(id);
	}

	public java.util.List<String> getRecentLogs(Developer developer, AiModel model) {
		return sessionDao.findAll().stream()
				.filter(s -> s.getDeveloper().getId() == developer.getId() && s.getModel().getId() == model.getId())
				.max((s1, s2) -> Integer.compare(s1.getId(), s2.getId()))
				.map(Session::getInteractionLog)
				.orElse(java.util.Collections.emptyList());
	}
}
