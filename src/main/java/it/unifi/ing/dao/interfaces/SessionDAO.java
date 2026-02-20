package it.unifi.ing.dao.interfaces;

import it.unifi.ing.domain.Session;

import java.util.List;

public interface SessionDAO {
	void save(Session session);
	Session findById(int id);
	List<Session> findByUser(int userId);
	List<Session> findAll();
	List<Session> findActiveSessions();
	void update(Session session);
	void delete(int id);
}
