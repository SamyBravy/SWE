package it.unifi.ing.business.services;

import it.unifi.ing.domain.GPU;
import it.unifi.ing.domain.GpuCluster;
import it.unifi.ing.domain.Subject;
import it.unifi.ing.domain.Observer;

public class LoadBalancerService implements Observer {

	private final SessionService sessionService;
	private final GpuCluster cluster;
	private LoadBalancingStrategy balancingStrategy;

	public LoadBalancerService(SessionService sessionService, GpuCluster cluster,
			LoadBalancingStrategy balancingStrategy) {
		this.sessionService = sessionService;
		this.cluster = cluster;
		this.balancingStrategy = balancingStrategy;
	}

	public void setBalancingStrategy(LoadBalancingStrategy balancingStrategy) {
		this.balancingStrategy = balancingStrategy;
	}

	@Override
	public void update(Subject subject, Object event) {
		if (subject instanceof GPU) {
			GPU gpu = (GPU) subject;
			onTemperatureAlert(gpu);
		} else if ("TICK".equals(event)) {
			balanceLoad();
		}
	}

	private void onTemperatureAlert(GPU gpu) {
		System.out.println("\n  ALERT: GPU " + gpu.getId()
				+ " exceeded 90°C! Temperature: " + String.format("%.1f", gpu.getTemperature()) + "°C");

		sessionService.detachGpuFromSession(gpu);

		cluster.releaseGpu(gpu);
		System.out.println("GPU " + gpu.getId() + " released and cooling down.");
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
		if (balancingStrategy != null) {
			balancingStrategy.balanceLoad(cluster, sessionService);
		}
	}
}
