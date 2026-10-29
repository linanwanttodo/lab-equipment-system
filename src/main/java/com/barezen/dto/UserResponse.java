package com.barezen.dto;

import com.barezen.entity.Role;

import java.time.LocalDateTime;

public class UserResponse {

    private final Long id;
    private final String username;
    private final String name;
    private final String contact;
    private final Role role;
    private final LocalDateTime createdAt;

    public UserResponse(Long id, String username, String name, String contact, Role role, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.contact = contact;
        this.role = role;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getName() { return name; }
    public String getContact() { return contact; }
    public Role getRole() { return role; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
