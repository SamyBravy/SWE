package it.unifi.ing.dao.interfaces;

import it.unifi.ing.domain.Sessione;

import java.util.List;

/**
 * DAO interface per la gestione delle Sessioni.
 */
public interface SessioneDAO {

	void save(Sessione sessione);

	Sessione findById(int id);

	List<Sessione> findByUtente(int utenteId);

	List<Sessione> findAll();

	List<Sessione> findActiveSessions();

	void update(Sessione sessione);

	void delete(int id);
}
