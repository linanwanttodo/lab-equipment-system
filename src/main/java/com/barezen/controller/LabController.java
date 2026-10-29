package com.barezen.controller;

import com.barezen.dto.LabRequest;
import com.barezen.dto.LabResponse;
import com.barezen.dto.MessageResponse;
import com.barezen.service.LabService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/labs")
public class LabController {

    private final LabService labService;

    public LabController(LabService labService) {
        this.labService = labService;
    }

    @GetMapping
    public List<LabResponse> list() {
        return labService.list();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public LabResponse create(@RequestBody LabRequest req) {
        return labService.create(req);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public LabResponse update(@PathVariable Long id, @RequestBody LabRequest req) {
        return labService.update(id, req);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public MessageResponse delete(@PathVariable Long id) {
        labService.delete(id);
        return new MessageResponse("删除成功");
    }
}
