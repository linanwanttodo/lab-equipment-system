package com.barezen.controller;

import com.barezen.dto.EquipmentRequest;
import com.barezen.dto.EquipmentResponse;
import com.barezen.dto.MessageResponse;
import com.barezen.entity.EquipmentStatus;
import com.barezen.repository.EquipmentRepository;
import com.barezen.service.EquipmentService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/equipment")
public class EquipmentController {

    private final EquipmentService equipmentService;
    private final EquipmentRepository equipmentRepository;

    public EquipmentController(EquipmentService equipmentService, EquipmentRepository equipmentRepository) {
        this.equipmentService = equipmentService;
        this.equipmentRepository = equipmentRepository;
    }

    @GetMapping
    public List<EquipmentResponse> list(@RequestParam(required = false) Long labId,
                                        @RequestParam(required = false) String category,
                                        @RequestParam(required = false) EquipmentStatus status,
                                        @RequestParam(required = false) String keyword) {
        return equipmentService.list(labId, category, status, keyword);
    }

    @GetMapping("/{id}")
    public EquipmentResponse get(@PathVariable Long id) {
        return equipmentService.get(id);
    }

    /** 类别去重列表（用于筛选下拉） */
    @GetMapping("/categories")
    public List<String> categories() {
        return equipmentRepository.findDistinctCategories();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public EquipmentResponse create(@Valid @RequestBody EquipmentRequest req) {
        return equipmentService.create(req);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public EquipmentResponse update(@PathVariable Long id, @Valid @RequestBody EquipmentRequest req) {
        return equipmentService.update(id, req);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public MessageResponse delete(@PathVariable Long id) {
        equipmentService.delete(id);
        return new MessageResponse("删除成功");
    }
}
