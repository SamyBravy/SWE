package it.unifi.ing.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * GPU: scheda grafica nel cluster. Funge da Subject nel pattern Observer.
 * Quando la temperatura supera 90°C, notifica tutti gli observer registrati.
 */
public class GPU {

    private static final double SOGLIA_TEMPERATURA = 90.0;

    private int id;
    private double temperatura;
    private double loadPercentage;
    private StatoGPU stato;
    private double vramCapacity;
    private Modello modelloCaricato;
    private final List<GpuObserver> observers;

    public GPU(int id) {
        this.id = id;
        this.temperatura = 30.0; // temperatura iniziale a riposo
        this.loadPercentage = 0.0;
        this.stato = StatoGPU.LIBERA;
        this.vramCapacity = 24.0; // 24 GB di VRAM di default
        this.modelloCaricato = null;
        this.observers = new ArrayList<>();
    }

    // --- Observer pattern ---

    public void addObserver(GpuObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public void removeObserver(GpuObserver observer) {
        observers.remove(observer);
    }

    private void notifyObservers() {
        for (GpuObserver observer : observers) {
            observer.onTemperatureAlert(this);
        }
    }

    // --- Getters / Setters ---

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getTemperatura() {
        return temperatura;
    }

    /**
     * Imposta la temperatura della GPU. Se supera la soglia (90°C),
     * lo stato diventa SURRISCALDATA e vengono notificati gli observer.
     */
    public void setTemperatura(double temperatura) {
        this.temperatura = temperatura;
        if (temperatura > SOGLIA_TEMPERATURA) {
            this.stato = StatoGPU.SURRISCALDATA;
            notifyObservers();
        }
    }

    public double getLoadPercentage() {
        return loadPercentage;
    }

    public void setLoadPercentage(double loadPercentage) {
        this.loadPercentage = loadPercentage;
    }

    public StatoGPU getStato() {
        return stato;
    }

    public void setStato(StatoGPU stato) {
        this.stato = stato;
    }

    public double getVramCapacity() {
        return vramCapacity;
    }

    public void setVramCapacity(double vramCapacity) {
        this.vramCapacity = vramCapacity;
    }

    public Modello getModelloCaricato() {
        return modelloCaricato;
    }

    public void setModelloCaricato(Modello modelloCaricato) {
        this.modelloCaricato = modelloCaricato;
    }

    public boolean isLibera() {
        return stato == StatoGPU.LIBERA;
    }

    /**
     * Simula un incremento di temperatura per tick del Timer.
     */
    public void simulaTick() {
        if (stato == StatoGPU.OCCUPATA) {
            // GPU sotto carico: temperatura sale casualmente
            double incremento = 1.0 + Math.random() * 3.0;
            setTemperatura(temperatura + incremento);
            // Simula carico proporzionale
            loadPercentage = Math.min(100.0, loadPercentage + Math.random() * 5.0);
        } else if (stato == StatoGPU.LIBERA) {
            // GPU a riposo: temperatura scende verso 30°C
            if (temperatura > 30.0) {
                double decremento = 0.5 + Math.random() * 1.5;
                this.temperatura = Math.max(30.0, temperatura - decremento);
            }
            // Carico scende verso 0
            loadPercentage = Math.max(0.0, loadPercentage - 5.0);
        }
    }

    @Override
    public String toString() {
        return "GPU [id=" + id + ", temp=" + String.format("%.1f", temperatura)
                + "°C, load=" + String.format("%.1f", loadPercentage)
                + "%, stato=" + stato + "]";
    }
}
