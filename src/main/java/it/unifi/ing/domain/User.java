package it.unifi.ing.domain;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Abstract base class for all system users.
 * UML: User (id, name, email, password)
 */
public abstract class User {

    private int id;
    private String name;
    private String email;
    private String password;

    protected User(int id, String name, String email, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public final void setPassword(String password) {
        this.password = BCrypt.hashpw(password, BCrypt.gensalt());
    }

    /**
     * Verifies user credentials using BCrypt.
     * UML: login(email, password) : boolean
     */
    public boolean login(String email, String pwd) {
        return this.email.equals(email) && BCrypt.checkpw(pwd, this.password);
    }

    /**
     * Returns the user's role in the system.
     */
    public abstract String getRole();

    @Override
    public String toString() {
        return getRole() + " [id=" + id + ", name=" + name + ", email=" + email + "]";
    }
}
