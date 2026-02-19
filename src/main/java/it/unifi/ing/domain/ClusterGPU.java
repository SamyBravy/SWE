package it.unifi.ing.domain;

import it.unifi.ing.dao.interfaces.GpuDAO;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ClusterGPU: Singleton che gestisce l'insieme delle GPU disponibili.
 * Unico punto di accesso all'hardware del cluster.
 */
public class ClusterGPU {

    private static ClusterGPU instance;

    private GpuDAO gpuDao;

    private ClusterGPU() {
        // costruttore privato per Singleton
    }

    /**
     * Restituisce l'unica istanza del ClusterGPU.
     */
    public static synchronized ClusterGPU getInstance() {
        if (instance == null) {
            instance = new ClusterGPU();
        }
        return instance;
    }

    /**
     * Inizializza il cluster con il DAO delle GPU.
     * Chiamato durante il bootstrapping in Main.
     */
    public void init(GpuDAO gpuDao) {
        this.gpuDao = gpuDao;
    }

    /**
     * Assegna una GPU libera dal cluster.
     * @return la GPU assegnata, oppure null se non ci sono GPU disponibili
     */
    public GPU assegnaGpu() {
        List<GPU> tutte = gpuDao.findAll();
        for (GPU gpu : tutte) {
            if (gpu.isLibera()) {
                gpu.setStato(StatoGPU.OCCUPATA);
                return gpu;
            }
        }
        return null;
    }

    /**
     * Rilascia una GPU, riportandola allo stato LIBERA.
     */
    public void rilasciaGpu(GPU gpu) {
        gpu.setStato(StatoGPU.LIBERA);
        gpu.setModelloCaricato(null);
    }

    /**
     * Restituisce la lista delle GPU attualmente libere.
     */
    public List<GPU> getGpuDisponibili() {
        return gpuDao.findAll().stream()
                .filter(GPU::isLibera)
                .collect(Collectors.toList());
    }

    /**
     * Restituisce tutte le GPU del cluster.
     */
    public List<GPU> getTutteLeGpu() {
        return gpuDao.findAll();
    }

    /**
     * Reset per i test.
     */
    public static synchronized void resetInstance() {
        instance = null;
    }
}
