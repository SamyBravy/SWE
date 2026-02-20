package it.unifi.ing.business.services;

import it.unifi.ing.dao.interfaces.AiModelDAO;
import it.unifi.ing.domain.AiModel;
import it.unifi.ing.domain.GPU;
import it.unifi.ing.domain.GpuCluster;
import it.unifi.ing.domain.ModelStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * Service for AI model verification by the Supervisor.
 * Supports: GPU loading, benchmarks, ethics tests, approval/rejection.
 */
public class VerificationService {

	private final AiModelDAO modelDao;
	private final GpuCluster cluster;

	public VerificationService(AiModelDAO modelDao, GpuCluster cluster) {
		this.modelDao = modelDao;
		this.cluster = cluster;
	}

	/**
	 * Loads a model on a GPU for verification.
	 * @return the assigned GPU, or null if none available
	 */
	public GPU loadOnGpu(AiModel model) {
		GPU gpu = cluster.getAvailableGpu();
		if (gpu != null) {
			gpu.setLoadedModel(model);
		}
		return gpu;
	}

	/**
	 * Runs benchmarks on the model using the assigned GPU.
	 * @return map with benchmark results (simulated)
	 */
	public Map<String, Object> runBenchmarks(AiModel model, GPU gpu) {
		Map<String, Object> results = new HashMap<>();
		results.put("avg_latency_ms", 45 + (int) (Math.random() * 50));
		results.put("throughput_tokens_sec", 100 + (int) (Math.random() * 200));
		results.put("memory_used_mb", 1024 + (int) (Math.random() * 2048));
		results.put("accuracy_percent", 85 + Math.random() * 15);
		results.put("model", model.getName());
		results.put("gpu_id", gpu.getId());
		return results;
	}

	/**
	 * Runs an ethics test on the model with a sample prompt.
	 * @return simulated model response
	 */
	public String runEthicsTest(AiModel model, String prompt) {
		return "[Model: " + model.getName() + "] Response to prompt: \"" + prompt
				+ (Math.random() < 0.5 ? " :)\"" : " :(\"");
	}

	/**
	 * Approves the model, setting the platform cost per token.
	 */
	public void approveModel(AiModel model, double costPerTokenPlatform) {
		model.setStatus(ModelStatus.APPROVED);
		model.setCostPerTokenPlatform(costPerTokenPlatform);
		modelDao.update(model);
	}

	/**
	 * Rejects the model with the provided reasons.
	 */
	public void rejectModel(AiModel model, String reasons) {
		model.setStatus(ModelStatus.REJECTED);
		model.setRejectionReasons(reasons);
		modelDao.update(model);
	}

	/**
	 * Releases a GPU used for verification.
	 */
	public void releaseGpu(GPU gpu) {
		cluster.releaseGpu(gpu);
	}
}
