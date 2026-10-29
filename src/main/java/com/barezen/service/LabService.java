package com.barezen.service;

import com.barezen.dto.LabRequest;
import com.barezen.dto.LabResponse;
import com.barezen.entity.Lab;
import com.barezen.exception.ApiException;
import com.barezen.repository.EquipmentRepository;
import com.barezen.repository.LabRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LabService {

    private final LabRepository labRepository;
    private final EquipmentRepository equipmentRepository;

    public LabService(LabRepository labRepository, EquipmentRepository equipmentRepository) {
        this.labRepository = labRepository;
        this.equipmentRepository = equipmentRepository;
    }

    @Transactional(readOnly = true)
    public List<LabResponse> list() {
        return labRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional
    public LabResponse create(LabRequest req) {
        if (req.getName() == null || req.getName().isBlank()) {
            throw new ApiException(400, "实验室名称不能为空");
        }
        Lab lab = new Lab();
        applyRequest(lab, req);
        lab = labRepository.save(lab);
        return toResponse(lab);
    }

    @Transactional
    public LabResponse update(Long id, LabRequest req) {
        Lab lab = labRepository.findById(id).orElseThrow(() -> new ApiException(404, "实验室不存在"));
        if (req.getName() != null && req.getName().isBlank()) {
            throw new ApiException(400, "实验室名称不能为空");
        }
        applyRequest(lab, req);
        lab = labRepository.save(lab);
        return toResponse(lab);
    }

    @Transactional
    public void delete(Long id) {
        Lab lab = labRepository.findById(id).orElseThrow(() -> new ApiException(404, "实验室不存在"));
        long equipmentCount = equipmentRepository.findAll().stream()
                .filter(e -> e.getLab() != null && id.equals(e.getLab().getId()))
                .count();
        if (equipmentCount > 0) {
            throw new ApiException(409, "该实验室下还有 " + equipmentCount + " 台设备，无法删除");
        }
        labRepository.delete(lab);
    }

    private void applyRequest(Lab lab, LabRequest req) {
        if (req.getName() != null) lab.setName(req.getName());
        if (req.getLocation() != null) lab.setLocation(req.getLocation());
        if (req.getManagerId() != null) lab.setManagerId(req.getManagerId());
    }

    private LabResponse toResponse(Lab l) {
        return new LabResponse(l.getId(), l.getName(), l.getLocation(), l.getManagerId(),
                l.getEquipments() != null ? l.getEquipments().size() : 0);
    }
}
