package com.barezen.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterRequest {

    @NotBlank
    @Size(min = 3, max = 50, message = "用户名长度 3-50")
    private String username;

    @NotBlank
    @Size(min = 6, max = 100, message = "密码长度至少 6 位")
    private String password;

    @NotBlank(message = "姓名不能为空")
    private String name;

    private String contact;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }
}
