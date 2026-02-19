package it.unifi.ing.business.services;

import it.unifi.ing.dao.interfaces.ModelloDAO;
import it.unifi.ing.domain.Modello;
import it.unifi.ing.domain.ModelProvider;
import it.unifi.ing.domain.StatoModello;

import java.util.List;

/**
 * Service per la gestione dei modelli AI: pubblicazione e recupero modelli in
 * attesa.
 */
public class ModelService {

	private final ModelloDAO modelloDao;
	private int nextId;

	public ModelService(ModelloDAO modelloDao) {
		this.modelloDao = modelloDao;
		this.nextId = 1;
	}

	/**
	 * Pubblica un nuovo modello AI nel sistema.
	 */
	public void publishModel(ModelProvider provider, String name, String desc,
			double cost, String safetensors, String json) {
		Modello modello = new Modello(nextId++, name, desc, cost, safetensors, json, provider);
		modelloDao.save(modello);
	}

	/**
	 * Restituisce la lista dei modelli in attesa di verifica.
	 */
	public List<Modello> getPendingModels() {
		return modelloDao.findByStato(StatoModello.IN_ATTESA);
	}

	/**
	 * Restituisce la lista dei modelli approvati.
	 */
	public List<Modello> getApprovedModels() {
		return modelloDao.findByStato(StatoModello.APPROVATO);
	}

	/**
	 * Restituisce tutti i modelli.
	 */
	public List<Modello> getAllModels() {
		return modelloDao.findAll();
	}

	/**
	 * Trova un modello per ID.
	 */
	public Modello findById(int id) {
		return modelloDao.findById(id);
	}
}
