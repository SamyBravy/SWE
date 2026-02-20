package it.unifi.ing.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Session: links a Developer, an AiModel, and a GPU.
 * UML: id, developer, model, gpu, startTimestamp, isActive
 */
public class Session {

	private int id;
	private Developer developer;
	private AiModel model;
	private GPU gpu;
	private LocalDateTime startTimestamp;
	private boolean active;
	private int totalTokensUsed;
	private List<String> interactionLog;

	public Session(int id, Developer developer, AiModel model, GPU gpu) {
		this.id = id;
		this.developer = developer;
		this.model = model;
		this.gpu = gpu;
		this.startTimestamp = LocalDateTime.now();
		this.active = true;
		this.totalTokensUsed = 0;
		this.interactionLog = new ArrayList<>();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Developer getDeveloper() {
		return developer;
	}

	public void setDeveloper(Developer developer) {
		this.developer = developer;
	}

	public AiModel getModel() {
		return model;
	}

	public void setModel(AiModel model) {
		this.model = model;
	}

	public GPU getGpu() {
		return gpu;
	}

	public void setGpu(GPU gpu) {
		this.gpu = gpu;
	}

	public LocalDateTime getStartTimestamp() {
		return startTimestamp;
	}

	public void setStartTimestamp(LocalDateTime startTimestamp) {
		this.startTimestamp = startTimestamp;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public int getTotalTokensUsed() {
		return totalTokensUsed;
	}

	public void addUsedTokens(int tokens) {
		this.totalTokensUsed += tokens;
	}

	/**
	 * UML: close()
	 */
	public void close() {
		this.active = false;
	}

	public void addLog(String log) {
		this.interactionLog.add(log);
	}

	public List<String> getInteractionLog() {
		return Collections.unmodifiableList(interactionLog);
	}

	@Override
	public String toString() {
		return "Session [id=" + id + ", developer=" + developer.getName()
				+ ", model=" + model.getName() + ", gpu=" + gpu.getId()
				+ ", active=" + active + ", tokens=" + totalTokensUsed + "]";
	}
}
