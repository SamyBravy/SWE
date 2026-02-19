package it.unifi.ing.business.services;

import it.unifi.ing.dao.memory.InMemoryReclamoDAO;
import it.unifi.ing.dao.memory.InMemoryUtenteDAO;
import it.unifi.ing.domain.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class ComplaintServiceTest {

	private ComplaintService complaintService;
	private InMemoryReclamoDAO reclamoDao;
	private Developer developer;
	private Modello modello;

	@BeforeEach
	void setUp() {
		reclamoDao = new InMemoryReclamoDAO();
		InMemoryUtenteDAO utenteDao = new InMemoryUtenteDAO();
		complaintService = new ComplaintService(reclamoDao, utenteDao);

		ModelProvider provider = new ModelProvider(1, "Prov", "prov@test.com", "pass");
		developer = new Developer(2, "Dev", "dev@test.com", "pass");
		developer.getWallet().addCredito(100.0);

		modello = new Modello(1, "TestModel", "Desc", 0.005, "s.bin", "c.json", provider);
		modello.setCostoPerTokenPiattaforma(0.005);
	}

	@Test
	void testAcceptComplaintConRimborso() {
		Reclamo reclamo = new Reclamo(1, developer, modello, "Modello non funziona",
				Arrays.asList("prompt1 -> errore"));
		reclamoDao.save(reclamo);

		double saldoPrima = developer.getWallet().getSaldo();
		complaintService.acceptComplaint(reclamo, 100, 0);

		assertEquals(StatoReclamo.ACCETTATO, reclamo.getStato());
		// 100 tokens * 0.01 costo totale = 1.0€ rimborsati
		assertEquals(saldoPrima + 1.0, developer.getWallet().getSaldo(), 0.001);
	}

	@Test
	void testAcceptComplaintConBloccoModello() {
		Reclamo reclamo = new Reclamo(1, developer, modello, "Modello pericoloso",
				Arrays.asList("prompt1 -> risposta non etica"));
		reclamoDao.save(reclamo);

		complaintService.acceptComplaint(reclamo, 0, 24); // blocca per 24 ore

		assertEquals(StatoReclamo.ACCETTATO, reclamo.getStato());
		assertEquals(StatoModello.BLOCCATO, modello.getStato());
	}

	@Test
	void testRejectComplaint() {
		Reclamo reclamo = new Reclamo(1, developer, modello, "Reclamo infondato",
				Arrays.asList("prompt1 -> risposta corretta"));
		reclamoDao.save(reclamo);

		complaintService.rejectComplaint(reclamo, "Risposta del modello era corretta");

		assertEquals(StatoReclamo.RIFIUTATO, reclamo.getStato());
		assertEquals("Risposta del modello era corretta", reclamo.getMotiviRifiuto());
	}

	@Test
	void testGetPendingComplaints() {
		Reclamo r1 = new Reclamo(1, developer, modello, "Reclamo 1", Arrays.asList("log1"));
		Reclamo r2 = new Reclamo(2, developer, modello, "Reclamo 2", Arrays.asList("log2"));
		r2.setStato(StatoReclamo.ACCETTATO);
		reclamoDao.save(r1);
		reclamoDao.save(r2);

		assertEquals(1, complaintService.getPendingComplaints().size());
	}

	@Test
	void testAcceptComplaintSenzaRimborso() {
		Reclamo reclamo = new Reclamo(1, developer, modello, "Problema lieve",
				Arrays.asList("log"));
		reclamoDao.save(reclamo);

		double saldoPrima = developer.getWallet().getSaldo();
		complaintService.acceptComplaint(reclamo, 0, 0);

		assertEquals(StatoReclamo.ACCETTATO, reclamo.getStato());
		assertEquals(saldoPrima, developer.getWallet().getSaldo(), 0.001);
	}
}
