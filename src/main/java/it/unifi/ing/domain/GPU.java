package it.unifi.ing.domain;

import java.util.ArrayList;
import java.util.List;

public final class GPU implements Subject {

    private static final double TEMPERATURE_THRESHOLD = 90.0;

    private int id;
    private double temperature;
    private double loadPercentage;
    private GpuStatus status;
    private double vramCapacity;
    private final List<Observer> observers;

    public GPU(int id) {
        this.id = id;
        this.temperature = 30.0;
        this.loadPercentage = 0.0;
        this.status = GpuStatus.INACTIVE;
        this.vramCapacity = 24.0;
        this.observers = new ArrayList<>();
    }

    @Override
    public void attach(Observer o) {
        //item 23: Check parameters for validity
        if (o != null && !observers.contains(o)) {
            observers.add(o);
        }
    }

    @Override
    public void detach(Observer o) {
        observers.remove(o);
    }

    @Override
    public void notifyObservers(Object event) {
        //Item 24: Defensive copy if an observer detaches itself during the updated loop
        List<Observer> observersCopy = new ArrayList<>(observers);
        for (Observer observer : observersCopy) {
            observer.update(this, event); //push observer
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
        if (temperature > TEMPERATURE_THRESHOLD) {
            this.status = GpuStatus.IDLE;
            notifyObservers("TEMPERATURE_ALERT");
        }
    }

    public double getLoadPercentage() {
        return loadPercentage;
    }

    public void setLoadPercentage(double loadPercentage) {
        this.loadPercentage = loadPercentage;
    }

    public GpuStatus getStatus() {
        return status;
    }

    public void setStatus(GpuStatus status) {
        this.status = status;
    }

    public double getVramCapacity() {
        return vramCapacity;
    }

    public void setVramCapacity(double vramCapacity) {
        this.vramCapacity = vramCapacity;
    }

    public boolean isAvailable() {
        return status == GpuStatus.INACTIVE;
    }

    public void simulateTick() {
        if (status == GpuStatus.ACTIVE) {
            double increment = 1.0 + Math.random() * 3.0;
            setTemperature(temperature + increment);
            loadPercentage = Math.min(100.0, loadPercentage + Math.random() * 5.0);
        } else if (status == GpuStatus.INACTIVE) {
            if (temperature > 30.0) {
                double decrement = 0.5 + Math.random() * 1.5;
                this.temperature = Math.max(30.0, temperature - decrement);
            }
            loadPercentage = Math.max(0.0, loadPercentage - 5.0);
        }
    }

    @Override
    public String toString() {
        return "GPU [id=" + id + ", temp=" + String.format("%.1f", temperature)
                + "°C, load=" + String.format("%.1f", loadPercentage)
                + "%, status=" + status + "]";
    }
}
