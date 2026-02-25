package it.unifi.ing.domain;

public interface Observer {
	void update(Subject subject, Object event);
}
