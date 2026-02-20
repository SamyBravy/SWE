package it.unifi.ing.business.services;

import it.unifi.ing.domain.GpuCluster;

/**
 * Strategy interface for GPU load balancing algorithms.
 */
public interface LoadBalancingStrategy {
	void balanceLoad(GpuCluster cluster, SessionService sessionService);
}
