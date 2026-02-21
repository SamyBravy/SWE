package it.unifi.ing.domain;

public final class Developer extends User {

	private Wallet wallet;

	public Developer(int id, String name, String email, String password) {
		super(id, name, email, password);
		this.wallet = new Wallet(id);
	}

	public Wallet getWallet() {
		return wallet;
	}

	public void setWallet(Wallet wallet) {
		this.wallet = wallet;
	}

	@Override
	public String getRole() {
		return "Developer";
	}
}
