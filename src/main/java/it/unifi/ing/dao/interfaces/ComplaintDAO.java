package it.unifi.ing.dao.interfaces;

import it.unifi.ing.domain.Complaint;
import it.unifi.ing.domain.ComplaintStatus;

import java.util.List;

public interface ComplaintDAO {
    void save(Complaint complaint);
    Complaint findById(int id);
    List<Complaint> findAll();
    List<Complaint> findByStatus(ComplaintStatus status);
    void update(Complaint complaint);
    void delete(int id);
}
