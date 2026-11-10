package com.barezen.lab.entity;

import java.sql.Timestamp;

/** 设备实体：status AVAILABLE 可借用 / MAINTENANCE 维护中 */
public class Equipment {
    private Long id;
    private String code;
    private Long labId;
    private String labName;
    private String name;
    private String category;
    private String status;
    private int total;
    private int available;
    private double deposit;
    private String description;
    private Timestamp createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public Long getLabId() { return labId; }
    public void setLabId(Long labId) { this.labId = labId; }
    public String getLabName() { return labName; }
    public void setLabName(String labName) { this.labName = labName; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public int getTotal() { return total; }
    public void setTotal(int total) { this.total = total; }
    public int getAvailable() { return available; }
    public void setAvailable(int available) { this.available = available; }
    public double getDeposit() { return deposit; }
    public void setDeposit(double deposit) { this.deposit = deposit; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
