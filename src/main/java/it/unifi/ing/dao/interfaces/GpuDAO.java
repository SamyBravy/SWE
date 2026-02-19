package it.unifi.ing.dao.interfaces;

import it.unifi.ing.domain.GPU;

import java.util.List;

/**
 * DAO interface per la gestione delle GPU.
 */
public interface GpuDAO {

	void save(GPU gpu);

	GPU findById(int id);

	List<GPU> findAll();

	void delete(int id);
}
