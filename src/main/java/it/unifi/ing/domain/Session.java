package it.unifi.ing.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Session: links a Developer, an AiModel, and a GPU.
 * UML: id, developer, model, gpu, unbilledUsedTokens, startTimestamp, isActive
 */
public class Session {

    private int id;
    private Developer developer;
    private AiModel model;
    private GPU gpu;
    private LocalDateTime startTimestamp;
    private LocalDateTime endTimestamp;
    private boolean active;
    private int unbilledUsedTokens;
    private int totalTokensUsed;
    private double totalCost;
    private List<String> interactionLog;

    public Session(int id, Developer developer, AiModel model, GPU gpu) {
        this.id = id;
        this.developer = developer;
        this.model = model;
        this.gpu = gpu;
        this.startTimestamp = LocalDateTime.now();
        this.endTimestamp = null;
        this.active = true;
        this.unbilledUsedTokens = 0;
        this.totalTokensUsed = 0;
        this.totalCost = 0.0;
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

    public LocalDateTime getEndTimestamp() {
        return endTimestamp;
    }

    public void setEndTimestamp(LocalDateTime endTimestamp) {
        this.endTimestamp = endTimestamp;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * UML: getUnbilledUsedTokens()
     */
    public int getUnbilledUsedTokens() {
        return unbilledUsedTokens;
    }

    public void setUnbilledUsedTokens(int unbilledUsedTokens) {
        this.unbilledUsedTokens = unbilledUsedTokens;
    }

    public int getTotalTokensUsed() {
        return totalTokensUsed;
    }

    public double getTotalCost() {
        return totalCost;
    }

    /**
     * UML: addTokens(count)
     */
    public void addTokens(int tokens) {
        this.unbilledUsedTokens += tokens;
        this.totalTokensUsed += tokens;
    }

    public void addTotalCost(double cost) {
        this.totalCost += cost;
    }

    /**
     * UML: resetTokens()
     */
    public void resetTokens() {
        this.unbilledUsedTokens = 0;
    }

    /**
     * UML: close()
     */
    public void close() {
        this.active = false;
        this.endTimestamp = LocalDateTime.now();
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
