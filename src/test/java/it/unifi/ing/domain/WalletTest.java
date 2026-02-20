package it.unifi.ing.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class WalletTest {

	private Wallet wallet;

	@BeforeEach
	void setUp() {
		wallet = new Wallet(1);
	}

	@Test
	void testInitialBalance() {
		assertEquals(0.0, wallet.getBalance());
		assertNotNull(wallet.getId());
	}

	@Test
	void testAddFunds() {
		wallet.addFunds(50.0);
		assertEquals(50.0, wallet.getBalance());
	}

	@Test
	void testCharge() {
		wallet.addFunds(100.0);
		assertTrue(wallet.charge(30.0));
		assertEquals(70.0, wallet.getBalance(), 0.001);
	}

	@Test
	void testChargeInsufficientFunds() {
		wallet.addFunds(10.0);
		assertFalse(wallet.charge(20.0));
		assertEquals(10.0, wallet.getBalance());
	}

	@Test
	void testAddFundsNegativeThrows() {
		assertThrows(IllegalArgumentException.class, () -> wallet.addFunds(-5.0));
	}

	@Test
	void testChargeNegativeThrows() {
		assertThrows(IllegalArgumentException.class, () -> wallet.charge(-5.0));
	}

	@Test
	void testTransactionHistory() {
		wallet.addFunds(100.0);
		wallet.charge(30.0);
		assertEquals(2, wallet.getTransactionHistory().size());
	}

	@Test
	void testTransactionHistoryImmutable() {
		wallet.addFunds(10.0);
		assertThrows(UnsupportedOperationException.class, () -> wallet.getTransactionHistory().add(
				new Transaction(99, 1.0, java.time.LocalDateTime.now(), "hack")));
	}

	@Test
	void testAddFundsWithReason() {
		wallet.addFunds(25.0, "REFUND");
		assertEquals(25.0, wallet.getBalance());
		assertEquals(1, wallet.getTransactionHistory().size());
	}
}
