package com.smartsociety.platform.complaint;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.*;

public interface ComplaintRepository extends JpaRepository<Complaint, UUID> {
    Page<Complaint> findByResidentId(UUID id, Pageable p);

    long countByCurrentStatus(com.smartsociety.platform.common.Enums.ComplaintStatus s);

    long countByDueAtBeforeAndCurrentStatusNot(Instant t, com.smartsociety.platform.common.Enums.ComplaintStatus s);

    @Query("select c.currentStatus, count(c) from Complaint c group by c.currentStatus")
    List<Object[]> countByStatus();

    @Query("select c.category.name, count(c) from Complaint c group by c.category.name order by count(c) desc")
    List<Object[]> countByCategory();


}
