package com.barezen.dto;

import com.barezen.entity.EquipmentStatus;

import java.time.LocalDateTime;

public class EquipmentResponse {

    private final Long id;
    private final String code;
    private final String name;
    private final String category;
    private final Long labId;
    private final String labName;
    private final int totalQty;
    private final int availableQty;
    private final Double deposit;
    private final EquipmentStatus status;
    private final String description;
    private final LocalDateTime createdAt;

    public EquipmentResponse(Long id, String code, String name, String category,
                             Long labId, String labName, int totalQty, int availableQty,
                             Double deposit, EquipmentStatus status, String description,
                             LocalDateTime createdAt) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.category = category;
        this.labId = labId;
        this.labName = labName;
        this.totalQty = totalQty;
        this.availableQty = availableQty;
        this.deposit = deposit;
        this.status = status;
        this.description = description;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public String getCode() { return code; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public Long getLabId() { return labId; }
    public String getLabName() { return labName; }
    public int getTotalQty() { return totalQty; }
    public int getAvailableQty() { return availableQty; }
    public Double getDeposit() { return deposit; }
    public EquipmentStatus getStatus() { return status; }
    public String getDescription() { return description; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
