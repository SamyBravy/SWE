package it.unifi.ing.domain;

import java.util.Objects;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Complaint: a report filed by a Developer regarding an AI model.
 * UML: id, developer, model, description, promptLogs, status, rejectionReasons
 */
public class Complaint {

    private int id;
    private Developer developer;
    private AiModel model;
    private String description;
    private List<String> promptLogs;
    private ComplaintStatus status;
    private String rejectionReasons;

    private Complaint(int id, Developer developer, AiModel model, String description, List<String> promptLogs) {
        this.id = id;
        this.developer = developer;
        this.model = model;
        this.description = description;
        this.promptLogs = new ArrayList<>(promptLogs);
        this.status = ComplaintStatus.PENDING_REVIEW;
        this.rejectionReasons = null;
    }

    //Item1
    public static Complaint submit(int id, Developer developer, AiModel model, String description, List<String> promptLogs) {
        if (id <= 0) {
            throw new IllegalArgumentException("Complaint ID must be positive");
        }
        Objects.requireNonNull(developer, "The developer filing the complaint cannot be null");
        Objects.requireNonNull(model, "The model being reported cannot be null");

        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Complaint description cannot be empty");
        }

        Objects.requireNonNull(promptLogs, "Prompt logs cannot be null");
        if (promptLogs.isEmpty()) {
            throw new IllegalArgumentException("A complaint must include at least one prompt log as evidence");
        }

        return new Complaint(id, developer, model, description, promptLogs);
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getPromptLogs() {
        return Collections.unmodifiableList(promptLogs);
    }

    public void setPromptLogs(List<String> promptLogs) {
        this.promptLogs = new ArrayList<>(promptLogs);
    }

    public ComplaintStatus getStatus() {
        return status;
    }

    public void setStatus(ComplaintStatus status) {
        this.status = status;
    }

    public String getRejectionReasons() {
        return rejectionReasons;
    }

    public void setRejectionReasons(String rejectionReasons) {
        this.rejectionReasons = rejectionReasons;
    }

    @Override
    public String toString() {
        return "Complaint [id=" + id + ", developer=" + developer.getName()
                + ", model=" + model.getName() + ", status=" + status + "]";
    }
}
