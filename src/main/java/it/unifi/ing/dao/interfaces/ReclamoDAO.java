package it.unifi.ing.dao.interfaces;

import it.unifi.ing.domain.Reclamo;
import it.unifi.ing.domain.StatoReclamo;

import java.util.List;

/**
 * DAO interface per la gestione dei Reclami.
 */
public interface ReclamoDAO {

    void save(Reclamo reclamo);

    Reclamo findById(int id);

    List<Reclamo> findAll();

    List<Reclamo> findByStato(StatoReclamo stato);

    void update(Reclamo reclamo);

    void delete(int id);
}
