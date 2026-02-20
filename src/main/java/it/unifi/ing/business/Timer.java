package it.unifi.ing.business;

import it.unifi.ing.domain.GPU;
import it.unifi.ing.domain.GpuCluster;
import it.unifi.ing.domain.Observer;

import java.util.ArrayList;
import java.util.List;

/**
 * Timer: Singleton that simulates a background thread
 * for periodic GPU temperature updates, token billing, and load balancing.
 * UML: instance, observers. Methods: getInstance(), tick()
 */
public class Timer {

	private static Timer instance;

	private Thread timerThread;
	private volatile boolean running;
	private final GpuCluster cluster;
	private final List<Observer> observers;
	private int intervalMs;

	private Timer(GpuCluster cluster) {
		this.cluster = cluster;
		this.running = false;
		this.observers = new ArrayList<>();
		this.intervalMs = 5000;
	}

	public static synchronized Timer getInstance(GpuCluster cluster) {
		if (instance == null) {
			instance = new Timer(cluster);
		}
		return instance;
	}

	public void addObserver(Observer observer) {
		if (!observers.contains(observer)) {
			observers.add(observer);
		}
	}

	public void removeObserver(Observer observer) {
		observers.remove(observer);
	}

	/**
	 * Backward-compatible service configuration.
	 */
	public void configureServices(Object billingService, Object loadBalancerService) {
		if (billingService instanceof Observer) {
			addObserver((Observer) billingService);
		}
		if (loadBalancerService instanceof Observer) {
			addObserver((Observer) loadBalancerService);
		}
	}

	public void start() {
		if (running) {
			return;
		}
		running = true;
		timerThread = new Thread(() -> {
			while (running) {
				try {
					Thread.sleep(intervalMs);
					tick();
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					break;
				}
			}
		}, "GPU-Timer-Thread");
		timerThread.setDaemon(true);
		timerThread.start();
	}

	public void tick() {
		for (GPU gpu : cluster.getAllGpus()) {
			gpu.simulateTick();
		}

		for (Observer observer : observers) {
			observer.update(this, "TICK");
		}
	}

	public void stop() {
		running = false;
		if (timerThread != null) {
			timerThread.interrupt();
		}
	}

	public boolean isRunning() {
		return running;
	}

	public void setIntervalMs(int intervalMs) {
		this.intervalMs = intervalMs;
	}

	public static synchronized void resetInstance() {
		if (instance != null) {
			instance.stop();
		}
		instance = null;
	}
}
