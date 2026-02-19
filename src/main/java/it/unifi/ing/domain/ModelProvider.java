package it.unifi.ing.domain;

/**
 * ModelProvider: utente che pubblica modelli AI nel sistema.
 */
public class ModelProvider extends Utente {

    public ModelProvider(int id, String nome, String email, String password) {
        super(id, nome, email, password);
    }

    @Override
    public String getRuolo() {
        return "ModelProvider";
    }
}
