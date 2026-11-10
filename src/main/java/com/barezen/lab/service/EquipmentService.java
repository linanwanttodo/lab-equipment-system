package com.barezen.lab.service;

import com.barezen.lab.dao.EquipmentDao;
import com.barezen.lab.entity.Equipment;
import com.barezen.lab.exception.ServiceException;

import java.util.List;

/** 设备业务：查询、管理员增删改 */
public class EquipmentService {

    private final EquipmentDao equipmentDao = new EquipmentDao();

    public List<Equipment> search(Long labId, String category, String status, String keyword) {
        return equipmentDao.search(labId, category, status, keyword);
    }

    public Equipment getById(Long id) {
        Equipment e = equipmentDao.findById(id);
        if (e == null) {
            throw new ServiceException("设备不存在");
        }
        return e;
    }

    public List<String> categories() {
        return equipmentDao.findAllCategories();
    }

    /** 新增设备（管理员）：业务校验 → 可借数量不得超过总数，状态默认可借 */
    public void add(Equipment e) {
        validate(e);
        if (e.getStatus() == null || e.getStatus().isEmpty()) {
            e.setStatus("AVAILABLE");
        }
        equipmentDao.save(e);
    }

    public void update(Equipment e) {
        if (e.getId() == null) {
            throw new ServiceException("设备 ID 不能为空");
        }
        validate(e);
        equipmentDao.update(e);
    }

    public void delete(Long id) {
        Equipment e = equipmentDao.findById(id);
        if (e == null) {
            throw new ServiceException("设备不存在");
        }
        equipmentDao.deleteById(id);
    }

    private void validate(Equipment e) {
        if (e.getName() == null || e.getName().trim().isEmpty()) {
            throw new ServiceException("设备名称不能为空");
        }
        if (e.getCategory() == null || e.getCategory().trim().isEmpty()) {
            throw new ServiceException("设备类别不能为空");
        }
        if (e.getTotal() < 0 || e.getAvailable() < 0) {
            throw new ServiceException("设备数量不能为负数");
        }
        if (e.getAvailable() > e.getTotal()) {
            throw new ServiceException("可借数量不得超过总数");
        }
    }
}
