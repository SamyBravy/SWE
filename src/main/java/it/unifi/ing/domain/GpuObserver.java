package it.unifi.ing.domain;

/**
 * Observer interface per il monitoraggio delle GPU.
 * Implementa il pattern Observer (Subject = GPU).
 */
public interface GpuObserver {

	/**
	 * Chiamato quando una GPU supera la soglia di temperatura (90°C).
	 * 
	 * @param gpu la GPU che ha generato l'allarme
	 */
	void onTemperatureAlert(GPU gpu);
}
