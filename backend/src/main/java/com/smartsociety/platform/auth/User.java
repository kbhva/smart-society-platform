package com.smartsociety.platform.auth;
import com.smartsociety.platform.common.Enums.Role; import jakarta.persistence.*; import java.time.Instant; import java.util.UUID;
@Entity @Table(name="users") public class User {
 @Id @GeneratedValue(strategy=GenerationType.UUID) private UUID id; @Column(nullable=false,unique=true) private String email; @Column(nullable=false) private String passwordHash; @Column(nullable=false) private String fullName; @Enumerated(EnumType.STRING) @Column(nullable=false) private Role role=Role.RESIDENT; @Column(nullable=false) private boolean enabled=true; @Column(nullable=false) private Instant createdAt=Instant.now();
 public UUID getId(){return id;} public String getEmail(){return email;} public void setEmail(String v){email=v;} public String getPasswordHash(){return passwordHash;} public void setPasswordHash(String v){passwordHash=v;} public String getFullName(){return fullName;} public void setFullName(String v){fullName=v;} public Role getRole(){return role;} public void setRole(Role v){role=v;} public boolean isEnabled(){return enabled;} public Instant getCreatedAt(){return createdAt;}
}
