package it.unifi.ing.business.services;

import it.unifi.ing.dao.interfaces.SessionDao;
import it.unifi.ing.domain.AiModel;
import it.unifi.ing.domain.Developer;
import it.unifi.ing.domain.GPU;
import it.unifi.ing.domain.GpuCluster;
import it.unifi.ing.domain.GpuStatus;
import it.unifi.ing.domain.Session;

public class SessionService {

	private final SessionDao sessionDao;
	private final GpuCluster cluster;
	private int nextId;

	public SessionService(SessionDao sessionDao, GpuCluster cluster) {
		this.sessionDao = sessionDao;
		this.cluster = cluster;
		this.nextId = 1;
	}

	public Session openSession(Developer developer, AiModel model) {
		GPU gpu = cluster.getAvailableGpu();
		if (gpu == null) {
			return null;
		}

		Session session = new Session(nextId++, developer, model, gpu);
		sessionDao.save(session);
		return session;
	}

	public String sendPrompt(Session session, String prompt) {
		if (!session.isActive()) {
			return "Error: session has already ended.";
		}

		boolean allOverheated = session.getGpus().stream().allMatch(g -> g.getStatus() == GpuStatus.IDLE);
		if (session.getGpus().isEmpty() || allOverheated) {
			return "Error: assigned GPUs are overheated or unavailable. Session will be terminated.";
		}

		int tokensConsumed = Math.max(5, (int) Math.ceil(prompt.length()));

		double promptCost = calculateCost(session, tokensConsumed);
		double availableBalance = session.getDeveloper().getWallet().getBalance();
		if (promptCost > availableBalance) {
			closeSession(session);
			return "Insufficient credit (need €" + String.format("%.2f", promptCost)
					+ ", available €" + String.format("%.2f", availableBalance)
					+ "). Session terminated automatically.";
		}

		boolean charged = session.getDeveloper().getWallet().charge(promptCost);
		if (!charged) {
			return "Error during charge. Session terminated.";
		}

		session.addUsedTokens(tokensConsumed);
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

		double totalCost = calculateCost(session, session.getTotalTokensUsed());

		session.close();
		sessionDao.update(session);
		for (GPU g : session.getGpus()) {
			cluster.releaseGpu(g);
		}

		return totalCost;
	}

	public Session findById(int id) {
		return sessionDao.findById(id);
	}

	public void detachGpuFromSession(GPU gpu) {
		for (Session session : sessionDao.findAll()) {
			if (session.isActive() && session.getGpus().contains(gpu)) {
				double lostLoad = gpu.getLoadPercentage();
				session.removeGpu(gpu);
				System.out.println("GPU " + gpu.getId() + " detached from session " + session.getId()
						+ " due to safety/overheating.");
				if (session.getGpus().isEmpty()) {
					System.out.println("Forced termination of session " + session.getId()
							+ " (no GPUs left for developer: " + session.getDeveloper().getName() + ")");
					closeSession(session);
				} else if (lostLoad > 0) {
					double addedLoad = lostLoad / session.getGpus().size();
					for (GPU remainingGpu : session.getGpus()) {
						remainingGpu.setLoadPercentage(Math.min(100.0, remainingGpu.getLoadPercentage() + addedLoad));
					}
					System.out.println("Lost load (" + String.format("%.1f", lostLoad)
							+ "%) redistributed evenly among surviving GPUs.");
				}
				return; // A GPU belongs exclusively to one session, we can exit early.
			}
		}
	}

	public java.util.List<Session> getAllSessions() {
		return sessionDao.findAll();
	}

	public java.util.List<Session> findByUser(int userId) {
		return sessionDao.findByUser(userId);
	}

	public double calculateCost(Session session, int tokensConsumed) {
		return tokensConsumed * session.getModel().getCostPerToken();
	}

	public java.util.List<String> getRecentLogs(Developer developer, AiModel model) {
		return sessionDao.findAll().stream()
				.filter(s -> s.getDeveloper().getId() == developer.getId() && s.getModel().getId() == model.getId())
				.max((s1, s2) -> Integer.compare(s1.getId(), s2.getId()))
				.map(Session::getInteractionLog)
				.orElse(java.util.Collections.emptyList());
	}
}
