package it.unifi.ing.domain;

/**
 * Supervisor: user who verifies, approves, or rejects AI models.
 */
public class Supervisor extends User {

    public Supervisor(int id, String name, String email, String password) {
        super(id, name, email, password);
    }

    @Override
    public String getRole() {
        return "Supervisor";
    }
}
