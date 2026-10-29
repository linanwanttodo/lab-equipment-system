package com.barezen.service;

import com.barezen.dto.EquipmentRequest;
import com.barezen.dto.EquipmentResponse;
import com.barezen.entity.Equipment;
import com.barezen.entity.EquipmentStatus;
import com.barezen.entity.LoanStatus;
import com.barezen.exception.ApiException;
import com.barezen.repository.EquipmentRepository;
import com.barezen.repository.LabRepository;
import com.barezen.repository.LoanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;
    private final LabRepository labRepository;
    private final LoanRepository loanRepository;

    public EquipmentService(EquipmentRepository equipmentRepository, LabRepository labRepository,
                            LoanRepository loanRepository) {
        this.equipmentRepository = equipmentRepository;
        this.labRepository = labRepository;
        this.loanRepository = loanRepository;
    }

    @Transactional(readOnly = true)
    public List<EquipmentResponse> list(Long labId, String category, EquipmentStatus status, String keyword) {
        return equipmentRepository.search(labId, category, status, keyword).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public EquipmentResponse get(Long id) {
        return toResponse(equipmentRepository.findById(id).orElseThrow(() -> new ApiException(404, "设备不存在")));
    }

    @Transactional
    public EquipmentResponse create(EquipmentRequest req) {
        if (equipmentRepository.existsByCode(req.getCode())) {
            throw new ApiException(409, "设备编号已存在: " + req.getCode());
        }
        Equipment eq = new Equipment();
        applyRequest(eq, req);
        if (eq.getTotalQty() <= 0) {
            eq.setTotalQty(1);
        }
        eq.setAvailableQty(eq.getTotalQty());
        eq = equipmentRepository.save(eq);
        return toResponse(eq);
    }

    @Transactional
    public EquipmentResponse update(Long id, EquipmentRequest req) {
        Equipment eq = equipmentRepository.findById(id).orElseThrow(() -> new ApiException(404, "设备不存在"));
        if (req.getCode() != null && !req.getCode().equals(eq.getCode())
                && equipmentRepository.existsByCode(req.getCode())) {
            throw new ApiException(409, "设备编号已存在: " + req.getCode());
        }
        // 修改总数时保持“已借出”数量不变
        int borrowed = eq.getTotalQty() - eq.getAvailableQty();
        applyRequest(eq, req);
        eq.setAvailableQty(Math.max(0, eq.getTotalQty() - borrowed));
        eq = equipmentRepository.save(eq);
        return toResponse(eq);
    }

    @Transactional
    public void delete(Long id) {
        Equipment eq = equipmentRepository.findById(id).orElseThrow(() -> new ApiException(404, "设备不存在"));
        boolean hasActive = loanRepository.findByStatus(LoanStatus.BORROWING).stream()
                .anyMatch(l -> l.getEquipment() != null && l.getEquipment().getId().equals(id));
        boolean hasOverdue = loanRepository.findByStatus(LoanStatus.OVERDUE).stream()
                .anyMatch(l -> l.getEquipment() != null && l.getEquipment().getId().equals(id));
        if (hasActive || hasOverdue) {
            throw new ApiException(409, "该设备存在未归还的借用，无法删除");
        }
        equipmentRepository.delete(eq);
    }

    private void applyRequest(Equipment eq, EquipmentRequest req) {
        if (req.getCode() != null) eq.setCode(req.getCode());
        if (req.getName() != null) eq.setName(req.getName());
        if (req.getCategory() != null) eq.setCategory(req.getCategory());
        if (req.getLabId() != null) {
            eq.setLab(labRepository.findById(req.getLabId()).orElse(null));
        }
        if (req.getTotalQty() != null) eq.setTotalQty(req.getTotalQty());
        if (req.getDeposit() != null) eq.setDeposit(req.getDeposit());
        if (req.getStatus() != null) eq.setStatus(req.getStatus());
        if (req.getDescription() != null) eq.setDescription(req.getDescription());
    }

    private EquipmentResponse toResponse(Equipment e) {
        return new EquipmentResponse(
                e.getId(), e.getCode(), e.getName(), e.getCategory(),
                e.getLab() != null ? e.getLab().getId() : null,
                e.getLab() != null ? e.getLab().getName() : null,
                e.getTotalQty(), e.getAvailableQty(),
                e.getDeposit(), e.getStatus(), e.getDescription(), e.getCreatedAt());
    }
}
