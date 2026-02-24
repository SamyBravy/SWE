package it.unifi.ing.domain;

import it.unifi.ing.business.services.Observer;

/**
 * Subject interface per il pattern Observer.
 * UML: attach(o: Observer), detach(o: Observer), notifyObservers(event: Object)
 */
public interface Subject {
	void attach(Observer o);

	void detach(Observer o);

	void notifyObservers(Object event);
}
