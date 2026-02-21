package it.unifi.ing.business.services;

import it.unifi.ing.domain.GPU;
import it.unifi.ing.domain.GpuCluster;
import it.unifi.ing.domain.GpuStatus;
import it.unifi.ing.domain.Session;

import java.util.List;

/**
 * Power-saving strategy: packs maximum load (up to 95%) onto the fewest number
 * of GPUs possible per Session, dynamically requesting or releasing hardware
 * explicitly on a per-session basis to reduce Watt consumption.
 */
public class EcoBalancingStrategy implements LoadBalancingStrategy {

	@Override
	public void balanceLoad(GpuCluster cluster, SessionService sessionService) {
		List<Session> activeSessions = sessionService.getAllSessions().stream().filter(Session::isActive).toList();

		for (Session session : activeSessions) {
			List<GPU> gpus = session.getGpus();
			if (gpus.isEmpty())
				continue;

			double totalLoad = gpus.stream().mapToDouble(GPU::getLoadPercentage).sum();

			int neededGpus = (int) Math.ceil(totalLoad / 95.0);
			if (neededGpus == 0)
				neededGpus = 1;

			// Scale up
			while (session.getGpus().size() < neededGpus) {
				GPU freeGpu = cluster.getAvailableGpu();
				if (freeGpu == null) {
					System.out.println("GPU shortage: cannot handle peak for Session " + session.getId());
					break;
				}
				session.addGpu(freeGpu);
				freeGpu.setStatus(GpuStatus.ACTIVE);
				System.out.println("GPU " + freeGpu.getId() + " activated to handle peak for Session "
						+ session.getId() + " (Eco Strategy).");
			}

			// Scale down
			while (session.getGpus().size() > neededGpus) {
				GPU removed = session.getGpus().get(session.getGpus().size() - 1);
				session.removeGpu(removed);
				cluster.releaseGpu(removed);
				System.out.println(
						"GPU " + removed.getId() + " detached from Session " + session.getId() + " to save power.");
			}

			// Pack load greedily
			double remainingLoad = totalLoad;
			for (int i = 0; i < session.getGpus().size(); i++) {
				GPU g = session.getGpus().get(i);
				if (i == session.getGpus().size() - 1) {
					g.setLoadPercentage(remainingLoad);
				} else {
					g.setLoadPercentage(95.0);
					remainingLoad -= 95.0;
				}
			}
		}
	}
}
