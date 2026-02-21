package it.unifi.ing.domain;

import it.unifi.ing.dao.interfaces.GpuDao;
import java.util.List;
import java.util.stream.Collectors;

public class GpuCluster {

    private static GpuCluster instance;
    private GpuDao gpuDao;

    //Item 2: enforce the singleton property with a private constructor
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

    public GPU getAvailableGpu() {
        List<GPU> all = gpuDao.findAll();
        for (GPU gpu : all) {
            if (gpu.isAvailable()) {
                gpu.setStatus(GpuStatus.ACTIVE);
                return gpu;
            }
        }
        return null;
    }

    //Releases a GPU, setting it back to ACTIVE.

    public void releaseGpu(GPU gpu) {
        gpu.setStatus(GpuStatus.INACTIVE);
    }

    //Returns all available (ACTIVE) GPUs.

    public List<GPU> getAvailableGpus() {
        return gpuDao.findAll().stream()
                .filter(GPU::isAvailable)
                .collect(Collectors.toList());
    }

    public List<GPU> getAllGpus() {
        return gpuDao.findAll();
    }

    public static synchronized void resetInstance() {
        instance = null;
    }
}
