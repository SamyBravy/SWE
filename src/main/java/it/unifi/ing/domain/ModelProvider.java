package it.unifi.ing.domain;

/**
 * ModelProvider: user who publishes AI models in the system.
 */
public class ModelProvider extends User {

    public ModelProvider(int id, String name, String email, String password) {
        super(id, name, email, password);
    }

    @Override
    public String getRole() {
        return "ModelProvider";
    }
}
