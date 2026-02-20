package it.unifi.ing.dao.interfaces;

import it.unifi.ing.domain.Complaint;
import it.unifi.ing.domain.ComplaintStatus;

import java.util.List;

public interface ComplaintDao extends GenericDao<Complaint> {
	List<Complaint> findByStatus(ComplaintStatus status);
}
