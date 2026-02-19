package it.unifi.ing.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Wallet: gestisce il saldo crediti e lo storico transazioni di un Developer.
 */
public class Wallet {

    private double saldo;
    private final List<Transaction> storicoTransazioni;
    private int nextTransactionId;

    public Wallet() {
        this.saldo = 0.0;
        this.storicoTransazioni = new ArrayList<>();
        this.nextTransactionId = 1;
    }

    public double getSaldo() {
        return saldo;
    }

    /**
     * Aggiunge credito al wallet.
     * @param importo importo da aggiungere (deve essere > 0)
     */
    public void addCredito(double importo) {
        if (importo <= 0) {
            throw new IllegalArgumentException("L'importo deve essere positivo");
        }
        this.saldo += importo;
        storicoTransazioni.add(new Transaction(
                nextTransactionId++, importo, LocalDateTime.now(),
                "RICARICA: +" + String.format("%.2f", importo)));
    }

    /**
     * Deduce credito dal wallet.
     * @param importo importo da detrarre (deve essere > 0 e <= saldo)
     * @return true se la deduzione è riuscita
     */
    public boolean deduciCredito(double importo) {
        if (importo <= 0) {
            throw new IllegalArgumentException("L'importo deve essere positivo");
        }
        if (importo > saldo) {
            return false;
        }
        this.saldo -= importo;
        storicoTransazioni.add(new Transaction(
                nextTransactionId++, -importo, LocalDateTime.now(),
                "ADDEBITO: -" + String.format("%.2f", importo)));
        return true;
    }

    /**
     * Aggiunge credito forzatamente (es. rimborso), anche se risulta in saldo negativo.
     * @param importo importo da aggiungere
     * @param motivo motivo della transazione
     */
    public void addCreditoConMotivo(double importo, String motivo) {
        this.saldo += importo;
        storicoTransazioni.add(new Transaction(
                nextTransactionId++, importo, LocalDateTime.now(), motivo));
    }

    public List<Transaction> getStoricoTransazioni() {
        return Collections.unmodifiableList(storicoTransazioni);
    }

    /**
     * Restituisce lo storico come lista di stringhe (backward compatible).
     */
    public List<String> getStoricoTransazioniStringhe() {
        List<String> result = new ArrayList<>();
        for (Transaction t : storicoTransazioni) {
            result.add(t.toString());
        }
        return Collections.unmodifiableList(result);
    }

    @Override
    public String toString() {
        return "Wallet [saldo=" + String.format("%.2f", saldo) + "]";
    }
}
