package it.unifi.ing.business;

import it.unifi.ing.business.services.BillingService;
import it.unifi.ing.business.services.LoadBalancerService;
import it.unifi.ing.domain.ClusterGPU;
import it.unifi.ing.domain.GPU;

/**
 * Timer: Singleton che simula un thread di background
 * per aggiornare periodicamente la temperatura delle GPU,
 * addebitare i token e bilanciare il carico.
 * Attore di sistema che gestisce il monitoraggio in tempo reale.
 */
public class Timer {

	private static Timer instance;

	private Thread timerThread;
	private volatile boolean running;
	private final ClusterGPU cluster;
	private BillingService billingService;
	private LoadBalancerService loadBalancerService;
	private int intervalloMs;

	private Timer(ClusterGPU cluster) {
		this.cluster = cluster;
		this.running = false;
		this.intervalloMs = 5000; // 5 secondi di default
	}

	/**
	 * Restituisce l'unica istanza del Timer.
	 */
	public static synchronized Timer getInstance(ClusterGPU cluster) {
		if (instance == null) {
			instance = new Timer(cluster);
		}
		return instance;
	}

	/**
	 * Configura i servizi di billing e load balancing da invocare ad ogni tick.
	 */
	public void configuraServizi(BillingService billingService, LoadBalancerService loadBalancerService) {
		this.billingService = billingService;
		this.loadBalancerService = loadBalancerService;
	}

	/**
	 * Avvia il timer in background.
	 */
	public void avvia() {
		if (running) {
			return;
		}
		running = true;
		timerThread = new Thread(() -> {
			while (running) {
				try {
					Thread.sleep(intervalloMs);
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

	/**
	 * Esegue un tick: aggiorna temperature, addebita token e bilancia il carico.
	 */
	private void tick() {
		// 1. Simula aggiornamento temperatura GPU
		for (GPU gpu : cluster.getTutteLeGpu()) {
			gpu.simulaTick();
		}

		// 2. Addebito periodico token (Use Case 6)
		if (billingService != null) {
			billingService.billActiveSessions();
		}

		// 3. Bilanciamento del carico (Use Case 5)
		if (loadBalancerService != null) {
			loadBalancerService.balanceLoad();
		}
	}

	/**
	 * Ferma il timer.
	 */
	public void ferma() {
		running = false;
		if (timerThread != null) {
			timerThread.interrupt();
		}
	}

	public boolean isRunning() {
		return running;
	}

	public void setIntervalloMs(int intervalloMs) {
		this.intervalloMs = intervalloMs;
	}

	/**
	 * Reset per i test.
	 */
	public static synchronized void resetInstance() {
		if (instance != null) {
			instance.ferma();
		}
		instance = null;
	}
}
