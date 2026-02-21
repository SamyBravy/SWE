package it.unifi.ing.business.services;

import it.unifi.ing.domain.GpuCluster;

/**
 * Strategy Pattern
 */

public interface LoadBalancingStrategy {
	void balanceLoad(GpuCluster cluster, SessionService sessionService);
}
