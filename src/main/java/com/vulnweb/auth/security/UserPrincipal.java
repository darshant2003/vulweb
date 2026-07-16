package com.vulnweb.auth.security;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.vulnweb.auth.entity.User;

public class UserPrincipal implements UserDetails {

    private final UUID id;
    private final String email;
    private final String password;
    private final boolean enabled;
    private final boolean emailVerified;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.enabled = user.isEnabled();
        this.emailVerified = user.isEmailVerified();
        this.authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    public UUID getId() {
        return id;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
