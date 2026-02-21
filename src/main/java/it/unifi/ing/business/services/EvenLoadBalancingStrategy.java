package it.unifi.ing.business.services;

import it.unifi.ing.domain.GPU;
import it.unifi.ing.domain.GpuCluster;
import it.unifi.ing.domain.GpuStatus;
import it.unifi.ing.domain.Session;

import java.util.List;

/**
 * Balances the total system load evenly across active GPUs but natively
 * scales horizontally by allocating extra GPUs to specific active Sessions
 * if their average load goes over 80%. Conversely, it detaches GPUs if
 * the session average goes below 20%.
 */
public class EvenLoadBalancingStrategy implements LoadBalancingStrategy {

	@Override
	public void balanceLoad(GpuCluster cluster, SessionService sessionService) {
		List<Session> activeSessions = sessionService.getAllSessions().stream().filter(Session::isActive).toList();

		for (Session session : activeSessions) {
			List<GPU> gpus = session.getGpus();
			if (gpus.isEmpty())
				continue;

			double totalLoad = gpus.stream().mapToDouble(GPU::getLoadPercentage).sum();
			double expectedLoad = totalLoad / gpus.size();

			if (expectedLoad > 80.0) {
				GPU freeGpu = cluster.getAvailableGpu();
				if (freeGpu != null) {
					session.addGpu(freeGpu);
					freeGpu.setStatus(GpuStatus.INACTIVE);
					System.out.println("⚡ GPU " + freeGpu.getId() + " activated for load balancing Session "
							+ session.getId() + ".");
					double newLoad = totalLoad / session.getGpus().size();
					session.getGpus().forEach(g -> g.setLoadPercentage(newLoad));
				} else {
					System.out.println(
							"⚠️ GPU shortage: load will be distributed uniformly ignoring max constraint for Session "
									+ session.getId());
					session.getGpus().forEach(g -> g.setLoadPercentage(expectedLoad));
				}
			} else if (expectedLoad < 20.0 && gpus.size() > 1) {
				GPU removed = gpus.get(gpus.size() - 1);
				session.removeGpu(removed);
				cluster.releaseGpu(removed);
				System.out.println("💤 GPU " + removed.getId() + " detached from Session " + session.getId()
						+ " (Even Balancing).");
				double newLoad = totalLoad / session.getGpus().size();
				session.getGpus().forEach(g -> g.setLoadPercentage(newLoad));
			} else {
				// Simply distribute the accumulated system load
				gpus.forEach(g -> g.setLoadPercentage(expectedLoad));
			}
		}
	}
}
