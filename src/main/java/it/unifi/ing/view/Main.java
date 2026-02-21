package it.unifi.ing.view;

import it.unifi.ing.business.Timer;
import it.unifi.ing.business.services.*;
import it.unifi.ing.controllers.*;
import it.unifi.ing.dao.interfaces.*;
import it.unifi.ing.dao.memory.*;
import it.unifi.ing.domain.*;
import java.util.Scanner;

public class Main {

	private static SessionService sessionService;
	private static ModelService modelService;
	private static ComplaintService complaintService;
	private static Scanner scanner;

	public static void main(String[] args) {
		scanner = new Scanner(System.in);

		// ===== 1. INITIALIZE DAOs =====
		UserDao userDao = new InMemoryUserDao();
		AiModelDao modelDao = new InMemoryAiModelDao();
		SessionDao sessionDao = new InMemorySessionDao();
		GpuDao gpuDao = new InMemoryGpuDao();
		ComplaintDao complaintDao = new InMemoryComplaintDao();

		// ===== 2. INITIALIZE GPU CLUSTER =====
		for (int i = 1; i <= 4; i++) {
			gpuDao.save(new GPU(i));
		}

		GpuCluster cluster = GpuCluster.getInstance();
		cluster.init(gpuDao);

		// ===== 3. CREATE SERVICES =====
		AuthService authService = new AuthService(userDao);
		modelService = new ModelService(modelDao);
		sessionService = new SessionService(sessionDao, cluster);
		VerificationService verificationService = new VerificationService(modelDao, cluster);
		complaintService = new ComplaintService(complaintDao);

		// LoadBalancerService (Observer) — register on all GPUs
		LoadBalancingStrategy balancingStrategy = new EvenLoadBalancingStrategy();
		LoadBalancerService loadBalancer = new LoadBalancerService(sessionService, cluster, balancingStrategy);
		loadBalancer.registerOnAllGpus();

		// ===== 4. START TIMER =====
		Timer timer = Timer.getInstance(cluster);
		timer.attach(loadBalancer);
		timer.start();

		// ===== 5. CREATE CONTROLLERS =====
		AuthController authController = new AuthController(authService, scanner);
		SessionController sessionController = new SessionController(sessionService, modelService, scanner);
		ModelController modelController = new ModelController(modelService, scanner);
		VerificationController verificationController = new VerificationController(verificationService, modelService,
				modelController, scanner);
		ComplaintController complaintController = new ComplaintController(complaintService, modelService,
				sessionService, scanner);
		WalletController walletController = new WalletController(scanner);
		StatsController statsController = new StatsController(sessionService, complaintService);

		NavigationController navigationController = new NavigationController(
				authController, sessionController, modelController,
				verificationController, complaintController, walletController,
				statsController, scanner);

		navigationController.start();
	}
}
