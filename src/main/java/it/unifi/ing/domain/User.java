package it.unifi.ing.domain;

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
        this.password = password;
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

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Verifies user credentials.
     * UML: login(email, password) : boolean
     */
    public boolean login(String email, String pwd) {
        return this.email.equals(email) && this.password.equals(pwd);
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
