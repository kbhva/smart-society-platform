package com.smartsociety.platform.security;
import com.smartsociety.platform.auth.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.*;
public record SecurityUser(User user) implements UserDetails { public Collection<? extends GrantedAuthority> getAuthorities(){return List.of(new SimpleGrantedAuthority("ROLE_"+user.getRole().name()));} public String getPassword(){return user.getPasswordHash();} public String getUsername(){return user.getEmail();} public boolean isEnabled(){return user.isEnabled();} public UUID id(){return user.getId();} }
