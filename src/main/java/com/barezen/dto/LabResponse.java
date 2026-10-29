package com.barezen.dto;

public class LabResponse {

    private final Long id;
    private final String name;
    private final String location;
    private final Long managerId;
    private final int equipmentCount;

    public LabResponse(Long id, String name, String location, Long managerId, int equipmentCount) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.managerId = managerId;
        this.equipmentCount = equipmentCount;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getLocation() { return location; }
    public Long getManagerId() { return managerId; }
    public int getEquipmentCount() { return equipmentCount; }
}
