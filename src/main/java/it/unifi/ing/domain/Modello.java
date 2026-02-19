package it.unifi.ing.domain;

/**
 * Modello AI pubblicato da un ModelProvider.
 */
public class Modello {

	private int id;
	private String nome;
	private String descrizione;
	private double costoPerTokenProvider;
	private String safetensorsPath;
	private String jsonPath;
	private StatoModello stato;
	private ModelProvider provider;
	private double costoPerTokenPiattaforma;

	public Modello(int id, String nome, String descrizione, double costoPerTokenProvider,
			String safetensorsPath, String jsonPath, ModelProvider provider) {
		this.id = id;
		this.nome = nome;
		this.descrizione = descrizione;
		this.costoPerTokenProvider = costoPerTokenProvider;
		this.safetensorsPath = safetensorsPath;
		this.jsonPath = jsonPath;
		this.stato = StatoModello.IN_ATTESA;
		this.provider = provider;
		this.costoPerTokenPiattaforma = 0.0;
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

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	/**
	 * Costo per token stabilito dal ModelProvider (compenso provider).
	 */
	public double getCostoPerTokenProvider() {
		return costoPerTokenProvider;
	}

	public void setCostoPerTokenProvider(double costoPerTokenProvider) {
		this.costoPerTokenProvider = costoPerTokenProvider;
	}

	public String getSafetensorsPath() {
		return safetensorsPath;
	}

	public void setSafetensorsPath(String safetensorsPath) {
		this.safetensorsPath = safetensorsPath;
	}

	public String getJsonPath() {
		return jsonPath;
	}

	public void setJsonPath(String jsonPath) {
		this.jsonPath = jsonPath;
	}

	public StatoModello getStato() {
		return stato;
	}

	public void setStato(StatoModello stato) {
		this.stato = stato;
	}

	public ModelProvider getProvider() {
		return provider;
	}

	public void setProvider(ModelProvider provider) {
		this.provider = provider;
	}

	/**
	 * Costo per token stabilito dal Supervisor (tariffa piattaforma/hosting).
	 */
	public double getCostoPerTokenPiattaforma() {
		return costoPerTokenPiattaforma;
	}

	public void setCostoPerTokenPiattaforma(double costoPerTokenPiattaforma) {
		this.costoPerTokenPiattaforma = costoPerTokenPiattaforma;
	}

	/**
	 * Costo totale per token pagato dal developer:
	 * compenso provider + tariffa piattaforma.
	 */
	public double getCostoTotalePerToken() {
		return costoPerTokenProvider + costoPerTokenPiattaforma;
	}

	@Override
	public String toString() {
		return "Modello [id=" + id + ", nome=" + nome + ", stato=" + stato
				+ ", costoProvider=€" + String.format("%.4f", costoPerTokenProvider)
				+ "/token, costoPiattaforma=€" + String.format("%.4f", costoPerTokenPiattaforma)
				+ "/token, provider=" + provider.getNome() + "]";
	}
}
