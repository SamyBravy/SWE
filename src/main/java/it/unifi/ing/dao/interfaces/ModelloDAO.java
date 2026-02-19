package it.unifi.ing.dao.interfaces;

import it.unifi.ing.domain.Modello;
import it.unifi.ing.domain.StatoModello;

import java.util.List;

/**
 * DAO interface per la gestione dei Modelli AI.
 */
public interface ModelloDAO {

	void save(Modello modello);

	Modello findById(int id);

	List<Modello> findAll();

	List<Modello> findByStato(StatoModello stato);

	void update(Modello modello);

	void delete(int id);
}
