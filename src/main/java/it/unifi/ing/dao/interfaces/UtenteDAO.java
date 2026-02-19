package it.unifi.ing.dao.interfaces;

import it.unifi.ing.domain.Utente;

import java.util.List;

/**
 * DAO interface per la gestione degli Utenti.
 */
public interface UtenteDAO {

	void save(Utente utente);

	Utente findById(int id);

	Utente findByEmail(String email);

	List<Utente> findAll();

	void delete(int id);
}
