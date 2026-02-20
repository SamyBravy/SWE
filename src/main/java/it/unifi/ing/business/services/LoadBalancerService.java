package it.unifi.ing.business.services;

import it.unifi.ing.dao.interfaces.SessionDAO;
import it.unifi.ing.domain.GPU;
import it.unifi.ing.domain.GpuCluster;
import it.unifi.ing.domain.GpuStatus;
import it.unifi.ing.domain.Observer;
import it.unifi.ing.domain.Session;

import java.util.List;

/**
 * LoadBalancerService: Observer for GPU monitoring.
 * On overheating alert, terminates the session and releases the GPU.
 * Also handles load balancing across GPUs.
 */
public class LoadBalancerService implements Observer {

	private final SessionDAO sessionDao;
	private final GpuCluster cluster;
	private final BillingService billingService;

	public LoadBalancerService(SessionDAO sessionDao, GpuCluster cluster, BillingService billingService) {
		this.sessionDao = sessionDao;
		this.cluster = cluster;
		this.billingService = billingService;
	}

	@Override
	public void update(Object subject, Object event) {
		if (subject instanceof GPU) {
			GPU gpu = (GPU) subject;
			onTemperatureAlert(gpu);
		}
	}

	private void onTemperatureAlert(GPU gpu) {
		System.out.println("\n⚠️  ALERT: GPU " + gpu.getId()
				+ " exceeded 90°C! Temperature: " + String.format("%.1f", gpu.getTemperature()) + "°C");

		List<Session> allSessions = sessionDao.findAll();
		for (Session session : allSessions) {
			if (session.isActive() && session.getGpu().getId() == gpu.getId()) {
				System.out.println("🔴 Forced termination of session " + session.getId()
						+ " for safety (developer: " + session.getDeveloper().getName() + ")");

				billingService.chargeCost(session);

				session.close();
				sessionDao.update(session);

				break;
			}
		}

		cluster.releaseGpu(gpu);
		System.out.println("✅ GPU " + gpu.getId() + " released and cooling down.");
	}

	/**
	 * Registers this observer on all GPUs in the cluster.
	 */
	public void registerOnAllGpus() {
		for (GPU gpu : cluster.getAllGpus()) {
			gpu.attach(this);
		}
	}

	/**
	 * Balances load across GPUs.
	 */
	public void balanceLoad() {
		List<GPU> allGpus = cluster.getAllGpus();
		if (allGpus.isEmpty()) {
			return;
		}

		List<GPU> activeGpus = allGpus.stream()
				.filter(g -> g.getStatus() == GpuStatus.INACTIVE)
				.toList();

		if (activeGpus.size() < 2) {
			return;
		}

		double totalLoad = activeGpus.stream()
				.mapToDouble(GPU::getLoadPercentage)
				.sum();

		double expectedLoad = totalLoad / activeGpus.size();

		if (expectedLoad > 80.0) {
			List<GPU> freeGpus = allGpus.stream()
					.filter(g -> g.getStatus() == GpuStatus.ACTIVE)
					.toList();

			for (GPU freeGpu : freeGpus) {
				freeGpu.setStatus(GpuStatus.INACTIVE);
				activeGpus = allGpus.stream()
						.filter(g -> g.getStatus() == GpuStatus.INACTIVE)
						.toList();
				expectedLoad = totalLoad / activeGpus.size();

				System.out.println("⚡ GPU " + freeGpu.getId() + " activated for load balancing.");

				if (expectedLoad <= 70.0) {
					break;
				}
			}

			if (expectedLoad > 80.0) {
				System.out.println("⚠️  GPU shortage: load will be distributed uniformly"
						+ " ignoring max load constraint.");
			}
		}

		List<GPU> finalGpus = allGpus.stream()
				.filter(g -> g.getStatus() == GpuStatus.INACTIVE)
				.toList();

		if (!finalGpus.isEmpty()) {
			double uniformLoad = totalLoad / finalGpus.size();
			for (GPU gpu : finalGpus) {
				gpu.setLoadPercentage(uniformLoad);
			}
			System.out.println("⚖️  Load balanced: " + String.format("%.1f", uniformLoad)
					+ "% on " + finalGpus.size() + " GPUs.");
		}
	}
}
