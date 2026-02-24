package it.unifi.ing.business.services;

import it.unifi.ing.domain.Subject;

public interface Observer {
	void update(Subject subject, Object event);
}
