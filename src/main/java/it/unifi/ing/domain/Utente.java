package it.unifi.ing.domain;

/**
 * Classe astratta base per tutti gli utenti del sistema.
 */
public abstract class Utente {

    private int id;
    private String nome;
    private String email;
    private String password;

    protected Utente(int id, String nome, String email, String password) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
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
     * Restituisce il ruolo dell'utente nel sistema.
     */
    public abstract String getRuolo();

    @Override
    public String toString() {
        return getRuolo() + " [id=" + id + ", nome=" + nome + ", email=" + email + "]";
    }
}
