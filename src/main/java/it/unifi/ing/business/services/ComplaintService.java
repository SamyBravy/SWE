package it.unifi.ing.business.services;

import it.unifi.ing.dao.interfaces.ReclamoDAO;
import it.unifi.ing.dao.interfaces.UtenteDAO;
import it.unifi.ing.domain.Modello;
import it.unifi.ing.domain.Reclamo;
import it.unifi.ing.domain.StatoModello;
import it.unifi.ing.domain.StatoReclamo;

import java.util.List;

/**
 * Service per la gestione dei reclami.
 * Permette al Supervisor di accettare o rifiutare reclami,
 * con possibilità di rimborso token e blocco modello.
 */
public class ComplaintService {

    private final ReclamoDAO reclamoDao;
    private final UtenteDAO utenteDao;

    public ComplaintService(ReclamoDAO reclamoDao, UtenteDAO utenteDao) {
        this.reclamoDao = reclamoDao;
        this.utenteDao = utenteDao;
    }

    /**
     * Restituisce i reclami in attesa di revisione.
     */
    public List<Reclamo> getPendingComplaints() {
        return reclamoDao.findByStato(StatoReclamo.IN_ATTESA);
    }

    /**
     * Restituisce tutti i reclami.
     */
    public List<Reclamo> getAllComplaints() {
        return reclamoDao.findAll();
    }

    /**
     * Trova un reclamo per ID.
     */
    public Reclamo findById(int id) {
        return reclamoDao.findById(id);
    }

    /**
     * Accetta un reclamo, rimborsando token all'utente e opzionalmente bloccando il modello.
     *
     * @param reclamo          il reclamo da accettare
     * @param tokenRimborsati  numero di token da rimborsare all'utente
     * @param bloccoOre        durata del blocco del modello in ore (0 = nessun blocco)
     */
    public void acceptComplaint(Reclamo reclamo, int tokenRimborsati, int bloccoOre) {
        reclamo.setStato(StatoReclamo.ACCETTATO);

        // Rimborsa i token al developer
        if (tokenRimborsati > 0) {
            double rimborso = tokenRimborsati * reclamo.getModello().getCostoTotalePerToken();
            reclamo.getDeveloper().getWallet().addCreditoConMotivo(rimborso,
                    "RIMBORSO reclamo #" + reclamo.getId() + ": " + tokenRimborsati + " token");
        }

        // Blocca il modello se richiesto
        if (bloccoOre > 0) {
            Modello modello = reclamo.getModello();
            modello.setStato(StatoModello.BLOCCATO);
            System.out.println("🔒 Modello '" + modello.getNome() + "' bloccato per " + bloccoOre + " ore.");
        }

        reclamoDao.update(reclamo);
    }

    /**
     * Rifiuta un reclamo con le motivazioni fornite.
     */
    public void rejectComplaint(Reclamo reclamo, String motivi) {
        reclamo.setStato(StatoReclamo.RIFIUTATO);
        reclamo.setMotiviRifiuto(motivi);
        reclamoDao.update(reclamo);
    }

    /**
     * Salva un nuovo reclamo nel sistema.
     */
    public void salvaReclamo(Reclamo reclamo) {
        reclamoDao.save(reclamo);
    }
}
