package it.unifi.ing.business.services;

import it.unifi.ing.dao.interfaces.UtenteDAO;
import it.unifi.ing.domain.Developer;
import it.unifi.ing.domain.ModelProvider;
import it.unifi.ing.domain.Supervisor;
import it.unifi.ing.domain.Utente;

/**
 * Service per la gestione dell'autenticazione: login e registrazione.
 */
public class AuthService {

	private final UtenteDAO utenteDao;
	private int nextId;

	public AuthService(UtenteDAO utenteDao) {
		this.utenteDao = utenteDao;
		this.nextId = 1;
	}

	/**
	 * Registra un nuovo utente nel sistema.
	 * 
	 * @param nome     nome dell'utente
	 * @param email    email (deve essere unica)
	 * @param password password
	 * @param ruolo    ruolo: "developer", "modelprovider", "supervisor"
	 * @return l'utente creato, oppure null se l'email è già registrata
	 */
	public Utente registra(String nome, String email, String password, String ruolo) {
		// Verifica unicità email
		if (utenteDao.findByEmail(email) != null) {
			return null;
		}

		Utente utente;
		switch (ruolo.toLowerCase()) {
			case "developer":
				utente = new Developer(nextId++, nome, email, password);
				break;
			case "modelprovider":
				utente = new ModelProvider(nextId++, nome, email, password);
				break;
			case "supervisor":
				utente = new Supervisor(nextId++, nome, email, password);
				break;
			default:
				return null;
		}

		utenteDao.save(utente);
		return utente;
	}

	/**
	 * Effettua il login con email e password.
	 * 
	 * @return l'utente se le credenziali sono corrette, null altrimenti
	 */
	public Utente login(String email, String password) {
		Utente utente = utenteDao.findByEmail(email);
		if (utente != null && utente.getPassword().equals(password)) {
			return utente;
		}
		return null;
	}
}
