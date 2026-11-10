package com.barezen.lab.entity;

import java.sql.Timestamp;

/** 用户实体：角色 ADMIN（管理员）/ STUDENT（学生） */
public class User {
    private Long id;
    private String username;
    private String password;
    private String name;
    private String role;
    private String contact;
    private Timestamp createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    /** 显示名：有姓名用姓名，否则用用户名 */
    public String getDisplayName() { return (name == null || name.isEmpty()) ? username : name; }

    public boolean isAdmin() { return "ADMIN".equals(role); }
}
