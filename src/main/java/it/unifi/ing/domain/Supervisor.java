package it.unifi.ing.domain;

/**
 * Supervisor: utente che verifica, approva o rifiuta i modelli AI.
 */
public class Supervisor extends Utente {

    public Supervisor(int id, String nome, String email, String password) {
        super(id, nome, email, password);
    }

    @Override
    public String getRuolo() {
        return "Supervisor";
    }
}
