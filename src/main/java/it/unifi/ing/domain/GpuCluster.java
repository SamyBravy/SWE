package it.unifi.ing.domain;

import it.unifi.ing.dao.interfaces.GpuDao;
import java.util.List;
import java.util.stream.Collectors;

/**
 * GpuCluster: Singleton managing all available GPUs.
 * UML: instance, gpus. Methods: getInstance(), getAvailableGpu(), getAllGpus()
 */
public class GpuCluster {

    private static GpuCluster instance;
    private GpuDao gpuDao;

    private GpuCluster() {
    }

    public static synchronized GpuCluster getInstance() {
        if (instance == null) {
            instance = new GpuCluster();
        }
        return instance;
    }

    public void init(GpuDao gpuDao) {
        this.gpuDao = gpuDao;
    }

    /**
     * Gets an available GPU and marks it as INACTIVE (in use).
     * UML: getAvailableGpu()
     */
    public GPU getAvailableGpu() {
        List<GPU> all = gpuDao.findAll();
        for (GPU gpu : all) {
            if (gpu.isAvailable()) {
                gpu.setStatus(GpuStatus.INACTIVE);
                return gpu;
            }
        }
        return null;
    }

    /**
     * Releases a GPU, setting it back to ACTIVE.
     */
    public void releaseGpu(GPU gpu) {
        gpu.setStatus(GpuStatus.ACTIVE);
    }

    /**
     * Returns all available (ACTIVE) GPUs.
     */
    public List<GPU> getAvailableGpus() {
        return gpuDao.findAll().stream()
                .filter(GPU::isAvailable)
                .collect(Collectors.toList());
    }

    /**
     * UML: getAllGpus()
     */
    public List<GPU> getAllGpus() {
        return gpuDao.findAll();
    }

    public static synchronized void resetInstance() {
        instance = null;
    }
}
