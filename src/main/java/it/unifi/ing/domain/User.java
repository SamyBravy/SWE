package it.unifi.ing.domain;

import org.mindrot.jbcrypt.BCrypt;

public abstract class User {

    private int id;
    private String name;
    private String email;
    private String password;

    protected User(int id, String name, String email, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = BCrypt.hashpw(password, BCrypt.gensalt()); //psw hashing to enhance security
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

    public boolean login(String email, String pwd) {
        return this.email.equals(email) && BCrypt.checkpw(pwd, this.password); //verify that the hash in the DB is equal to the hash calculated
    }

    public abstract String getRole();

    @Override
    public String toString() {
        return getRole() + " [id=" + id + ", name=" + name + ", email=" + email + "]";
    }
}
