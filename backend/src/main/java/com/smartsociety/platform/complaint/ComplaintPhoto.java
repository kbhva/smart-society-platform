package com.smartsociety.platform.complaint;

import com.smartsociety.platform.auth.User;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name="complaint_photos")
public class ComplaintPhoto {
    @Id @GeneratedValue(strategy=GenerationType.UUID) UUID id;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="complaint_id") Complaint complaint;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="uploaded_by") User uploadedBy;
    @Column(nullable=false) String storageKey;
    @Column(nullable=false) String originalFilename;
    @Column(nullable=false) String mimeType;
    @Column(nullable=false) long fileSize;
    Instant createdAt=Instant.now();
    public UUID getId(){return id;} public Complaint getComplaint(){return complaint;} public User getUploadedBy(){return uploadedBy;}
    public String getStorageKey(){return storageKey;} public String getOriginalFilename(){return originalFilename;} public String getMimeType(){return mimeType;} public long getFileSize(){return fileSize;} public Instant getCreatedAt(){return createdAt;}
    public void setComplaint(Complaint x){complaint=x;} public void setUploadedBy(User x){uploadedBy=x;} public void setStorageKey(String x){storageKey=x;} public void setOriginalFilename(String x){originalFilename=x;} public void setMimeType(String x){mimeType=x;} public void setFileSize(long x){fileSize=x;}
}
