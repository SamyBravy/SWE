package it.unifi.ing.domain;

/**
 * Observer interface per il pattern Observer.
 * UML: update(subject: Subject, event: Object) : void
 */
public interface Observer {
	void update(Subject subject, Object event);
}
