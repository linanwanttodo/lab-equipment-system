package com.barezen.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * 携带数值 id 的自定义主体，便于 Controller 直接拿到 userId；
 * 权限用 ROLE_ 前缀，配合 @PreAuthorize("hasRole('ADMIN')")。
 */
public class AppUser implements UserDetails {

    private final Long id;
    private final String username;
    private final String password;
    private final String name;
    private final String role;

    public AppUser(Long id, String username, String password, String name, String role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.name = name;
        this.role = role;
    }

    public Long getId() { return id; }
    public String getDisplayName() { return name; }
    public String getRole() { return role; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role));
    }

    @Override
    public String getPassword() { return password; }

    @Override
    public String getUsername() { return username; }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}
