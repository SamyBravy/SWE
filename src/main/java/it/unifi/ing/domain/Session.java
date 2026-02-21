package it.unifi.ing.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Session {

	private int id;
	private Developer developer;
	private AiModel model;
	private List<GPU> gpus; //one Session can use more than one GPU
	private LocalDateTime startTimestamp;
	private boolean active;
	private int totalTokensUsed;
	private List<String> interactionLog;

	public Session(int id, Developer developer, AiModel model, GPU initialGpu) {
		this.id = id;
		this.developer = developer;
		this.model = model;
		this.gpus = new ArrayList<>();
		if (initialGpu != null) {
			this.gpus.add(initialGpu);
		}
		this.startTimestamp = LocalDateTime.now();
		this.active = true;
		this.totalTokensUsed = 0;
		this.interactionLog = new ArrayList<>(); //grows dynamically
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

	public List<GPU> getGpus() {
		return Collections.unmodifiableList(gpus);
	}

	public void addGpu(GPU gpu) {
		if (!this.gpus.contains(gpu)) {
			this.gpus.add(gpu);
		}
	}

	public void removeGpu(GPU gpu) {
		this.gpus.remove(gpu);
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

	public void activate() {
		this.active = true;
	}

	public void close() {
		this.active = false;
	}

	public int getTotalTokensUsed() {
		return totalTokensUsed;
	}

	public void addUsedTokens(int tokens) {
		this.totalTokensUsed += tokens;
	}

	public void resetTokens() {
		this.totalTokensUsed = 0;
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
				+ ", model=" + model.getName() + ", gpus=" + gpus.size()
				+ ", active=" + active + ", tokens=" + totalTokensUsed + "]";
	}
}
