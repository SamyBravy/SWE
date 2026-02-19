package it.unifi.ing.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Reclamo: segnalazione di un Developer riguardo un modello AI.
 * Contiene la descrizione del reclamo, i log dei prompt e lo stato di revisione.
 */
public class Reclamo {

    private int id;
    private Developer developer;
    private Modello modello;
    private String descrizione;
    private List<String> promptLogs;
    private StatoReclamo stato;
    private String motiviRifiuto;

    public Reclamo(int id, Developer developer, Modello modello, String descrizione, List<String> promptLogs) {
        this.id = id;
        this.developer = developer;
        this.modello = modello;
        this.descrizione = descrizione;
        this.promptLogs = new ArrayList<>(promptLogs);
        this.stato = StatoReclamo.IN_ATTESA;
        this.motiviRifiuto = null;
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

    public Modello getModello() {
        return modello;
    }

    public void setModello(Modello modello) {
        this.modello = modello;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public List<String> getPromptLogs() {
        return Collections.unmodifiableList(promptLogs);
    }

    public void setPromptLogs(List<String> promptLogs) {
        this.promptLogs = new ArrayList<>(promptLogs);
    }

    public StatoReclamo getStato() {
        return stato;
    }

    public void setStato(StatoReclamo stato) {
        this.stato = stato;
    }

    public String getMotiviRifiuto() {
        return motiviRifiuto;
    }

    public void setMotiviRifiuto(String motiviRifiuto) {
        this.motiviRifiuto = motiviRifiuto;
    }

    @Override
    public String toString() {
        return "Reclamo [id=" + id + ", developer=" + developer.getNome()
                + ", modello=" + modello.getNome() + ", stato=" + stato + "]";
    }
}
