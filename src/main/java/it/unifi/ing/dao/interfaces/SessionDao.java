package it.unifi.ing.dao.interfaces;

import it.unifi.ing.domain.Session;

import java.util.List;

public interface SessionDao extends GenericDao<Session> {
	List<Session> findByUser(int userId);
	List<Session> findActiveSessions();
}
