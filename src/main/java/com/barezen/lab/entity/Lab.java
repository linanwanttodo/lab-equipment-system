package com.barezen.lab.entity;

import java.sql.Timestamp;

/** 实验室实体 */
public class Lab {
    private Long id;
    private String name;
    private String location;
    private Timestamp createdAt;
    private int equipmentCount;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public int getEquipmentCount() { return equipmentCount; }
    public void setEquipmentCount(int equipmentCount) { this.equipmentCount = equipmentCount; }
}
