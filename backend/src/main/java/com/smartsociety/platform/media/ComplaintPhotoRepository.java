package com.smartsociety.platform.media;

import com.smartsociety.platform.complaint.ComplaintPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface ComplaintPhotoRepository extends JpaRepository<ComplaintPhoto,UUID> {
    List<ComplaintPhoto> findByComplaintIdOrderByCreatedAtAsc(UUID complaintId);
    Optional<ComplaintPhoto> findByIdAndComplaintId(UUID id, UUID complaintId);
}
