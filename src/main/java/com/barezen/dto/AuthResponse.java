package com.barezen.dto;

import com.barezen.entity.Role;

public class AuthResponse {

    private final String token;
    private final Long id;
    private final String username;
    private final String name;
    private final Role role;

    public AuthResponse(String token, Long id, String username, String name, Role role) {
        this.token = token;
        this.id = id;
        this.username = username;
        this.name = name;
        this.role = role;
    }

    public String getToken() { return token; }
    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getName() { return name; }
    public Role getRole() { return role; }
}
