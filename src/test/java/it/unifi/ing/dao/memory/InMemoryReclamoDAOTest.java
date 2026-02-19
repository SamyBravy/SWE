package it.unifi.ing.dao.memory;

import it.unifi.ing.domain.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryReclamoDAOTest {

	private InMemoryReclamoDAO reclamoDao;
	private Developer developer;
	private Modello modello;

	@BeforeEach
	void setUp() {
		reclamoDao = new InMemoryReclamoDAO();
		ModelProvider provider = new ModelProvider(1, "Prov", "prov@test.com", "pass");
		developer = new Developer(2, "Dev", "dev@test.com", "pass");
		modello = new Modello(1, "Modello1", "Desc", 0.01, "s.bin", "c.json", provider);
	}

	@Test
	void testSaveAndFindById() {
		Reclamo reclamo = new Reclamo(1, developer, modello, "Test", Arrays.asList("log1"));
		reclamoDao.save(reclamo);

		Reclamo trovato = reclamoDao.findById(1);
		assertNotNull(trovato);
		assertEquals(1, trovato.getId());
	}

	@Test
	void testFindByIdNonEsistente() {
		assertNull(reclamoDao.findById(999));
	}

	@Test
	void testFindAll() {
		reclamoDao.save(new Reclamo(1, developer, modello, "Reclamo 1", Arrays.asList("log1")));
		reclamoDao.save(new Reclamo(2, developer, modello, "Reclamo 2", Arrays.asList("log2")));

		assertEquals(2, reclamoDao.findAll().size());
	}

	@Test
	void testFindByStato() {
		Reclamo r1 = new Reclamo(1, developer, modello, "Reclamo 1", Arrays.asList("log1"));
		Reclamo r2 = new Reclamo(2, developer, modello, "Reclamo 2", Arrays.asList("log2"));
		r2.setStato(StatoReclamo.ACCETTATO);

		reclamoDao.save(r1);
		reclamoDao.save(r2);

		List<Reclamo> pendenti = reclamoDao.findByStato(StatoReclamo.IN_ATTESA);
		assertEquals(1, pendenti.size());
		assertEquals(1, pendenti.get(0).getId());

		List<Reclamo> accettati = reclamoDao.findByStato(StatoReclamo.ACCETTATO);
		assertEquals(1, accettati.size());
	}

	@Test
	void testDelete() {
		reclamoDao.save(new Reclamo(1, developer, modello, "Test", Arrays.asList("log1")));
		reclamoDao.delete(1);
		assertNull(reclamoDao.findById(1));
	}

	@Test
	void testUpdate() {
		Reclamo reclamo = new Reclamo(1, developer, modello, "Test", Arrays.asList("log1"));
		reclamoDao.save(reclamo);

		reclamo.setStato(StatoReclamo.RIFIUTATO);
		reclamoDao.update(reclamo);

		assertEquals(StatoReclamo.RIFIUTATO, reclamoDao.findById(1).getStato());
	}
}
