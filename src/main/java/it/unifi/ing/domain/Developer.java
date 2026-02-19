package it.unifi.ing.domain;

/**
 * Developer: utente che utilizza i modelli AI e gestisce crediti tramite Wallet.
 */
public class Developer extends Utente {

    private Wallet wallet;

    public Developer(int id, String nome, String email, String password) {
        super(id, nome, email, password);
        this.wallet = new Wallet();
    }

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    @Override
    public String getRuolo() {
        return "Developer";
    }
}
