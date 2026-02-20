package it.unifi.ing.dao.memory;

import it.unifi.ing.dao.interfaces.ComplaintDAO;
import it.unifi.ing.domain.Complaint;
import it.unifi.ing.domain.ComplaintStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InMemoryComplaintDAO implements ComplaintDAO {

    private final Map<Integer, Complaint> storage = new HashMap<>();

    @Override
    public void save(Complaint complaint) {
        storage.put(complaint.getId(), complaint);
    }

    @Override
    public Complaint findById(int id) {
        return storage.get(id);
    }

    @Override
    public List<Complaint> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public List<Complaint> findByStatus(ComplaintStatus status) {
        return storage.values().stream()
                .filter(c -> c.getStatus() == status)
                .collect(Collectors.toList());
    }

    @Override
    public void update(Complaint complaint) {
        storage.put(complaint.getId(), complaint);
    }

    @Override
    public void delete(int id) {
        storage.remove(id);
    }
}
