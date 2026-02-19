package it.unifi.ing.dao.memory;

import it.unifi.ing.domain.ModelProvider;
import it.unifi.ing.domain.Modello;
import it.unifi.ing.domain.StatoModello;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryModelloDAOTest {

	private InMemoryModelloDAO dao;
	private ModelProvider provider;

	@BeforeEach
	void setUp() {
		dao = new InMemoryModelloDAO();
		provider = new ModelProvider(1, "Provider", "prov@test.com", "pass");
	}

	@Test
	void testSaveAndFindById() {
		Modello m = new Modello(1, "Model1", "Desc", 10.0, "s.bin", "c.json", provider);
		dao.save(m);
		assertNotNull(dao.findById(1));
		assertEquals("Model1", dao.findById(1).getNome());
	}

	@Test
	void testFindByStato() {
		Modello m1 = new Modello(1, "Model1", "Desc", 10.0, "s.bin", "c.json", provider);
		Modello m2 = new Modello(2, "Model2", "Desc", 10.0, "s.bin", "c.json", provider);
		m2.setStato(StatoModello.APPROVATO);
		dao.save(m1);
		dao.save(m2);

		List<Modello> inAttesa = dao.findByStato(StatoModello.IN_ATTESA);
		List<Modello> approvati = dao.findByStato(StatoModello.APPROVATO);

		assertEquals(1, inAttesa.size());
		assertEquals(1, approvati.size());
		assertEquals("Model1", inAttesa.get(0).getNome());
		assertEquals("Model2", approvati.get(0).getNome());
	}

	@Test
	void testUpdate() {
		Modello m = new Modello(1, "Model1", "Desc", 10.0, "s.bin", "c.json", provider);
		dao.save(m);
		m.setStato(StatoModello.APPROVATO);
		dao.update(m);
		assertEquals(StatoModello.APPROVATO, dao.findById(1).getStato());
	}

	@Test
	void testFindAll() {
		dao.save(new Modello(1, "M1", "D", 5.0, "s", "j", provider));
		dao.save(new Modello(2, "M2", "D", 5.0, "s", "j", provider));
		assertEquals(2, dao.findAll().size());
	}

	@Test
	void testDelete() {
		dao.save(new Modello(1, "M1", "D", 5.0, "s", "j", provider));
		dao.delete(1);
		assertNull(dao.findById(1));
	}
}
