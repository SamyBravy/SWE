package it.unifi.ing.domain;

/**
 * AI Model published by a ModelProvider.
 * UML: AiModel (id, name, description, costPerToken, status, safetensorsFile, jsonFile, provider)
 */
public class AiModel {

	private int id;
	private String name;
	private String description;
	private double costPerTokenProvider;
	private String safetensorsFile;
	private String jsonFile;
	private ModelStatus status;
	private ModelProvider provider;
	private double costPerTokenPlatform;
	private String rejectionReasons;

	public AiModel(int id, String name, String description, double costPerTokenProvider,
			String safetensorsFile, String jsonFile, ModelProvider provider) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.costPerTokenProvider = costPerTokenProvider;
		this.safetensorsFile = safetensorsFile;
		this.jsonFile = jsonFile;
		this.status = ModelStatus.PENDING_REVIEW;
		this.provider = provider;
		this.costPerTokenPlatform = 0.0;
		this.rejectionReasons = null;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public double getCostPerTokenProvider() {
		return costPerTokenProvider;
	}

	public void setCostPerTokenProvider(double costPerTokenProvider) {
		this.costPerTokenProvider = costPerTokenProvider;
	}

	public String getSafetensorsFile() {
		return safetensorsFile;
	}

	public void setSafetensorsFile(String safetensorsFile) {
		this.safetensorsFile = safetensorsFile;
	}

	public String getJsonFile() {
		return jsonFile;
	}

	public void setJsonFile(String jsonFile) {
		this.jsonFile = jsonFile;
	}

	public ModelStatus getStatus() {
		return status;
	}

	public void setStatus(ModelStatus status) {
		this.status = status;
	}

	public ModelProvider getProvider() {
		return provider;
	}

	public void setProvider(ModelProvider provider) {
		this.provider = provider;
	}

	public double getCostPerTokenPlatform() {
		return costPerTokenPlatform;
	}

	public void setCostPerTokenPlatform(double costPerTokenPlatform) {
		this.costPerTokenPlatform = costPerTokenPlatform;
	}

	/**
	 * Total cost per token paid by the developer:
	 * provider fee + platform fee.
	 * UML: getCostPerToken()
	 */
	public double getCostPerToken() {
		return costPerTokenProvider + costPerTokenPlatform;
	}

	public String getRejectionReasons() {
		return rejectionReasons;
	}

	public void setRejectionReasons(String rejectionReasons) {
		this.rejectionReasons = rejectionReasons;
	}

	@Override
	public String toString() {
		return "AiModel [id=" + id + ", name=" + name + ", description=" + description
				+ ", status=" + status
				+ ", costProvider=€" + String.format("%.4f", costPerTokenProvider)
				+ "/token, costPlatform=€" + String.format("%.4f", costPerTokenPlatform)
				+ "/token, provider=" + provider.getName() + "]";
	}
}
