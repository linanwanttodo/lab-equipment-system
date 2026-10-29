package com.barezen.dto;

import com.barezen.entity.EquipmentStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class EquipmentRequest {

    @NotBlank
    private String code;

    @NotBlank
    private String name;

    private String category;

    private Long labId;

    @Min(value = 1, message = "总数至少为 1")
    private Integer totalQty;

    private Double deposit;

    private EquipmentStatus status;

    private String description;

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public Long getLabId() { return labId; }
    public void setLabId(Long labId) { this.labId = labId; }
    public Integer getTotalQty() { return totalQty; }
    public void setTotalQty(Integer totalQty) { this.totalQty = totalQty; }
    public Double getDeposit() { return deposit; }
    public void setDeposit(Double deposit) { this.deposit = deposit; }
    public EquipmentStatus getStatus() { return status; }
    public void setStatus(EquipmentStatus status) { this.status = status; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
