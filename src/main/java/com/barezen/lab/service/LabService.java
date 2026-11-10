package com.barezen.lab.service;

import com.barezen.lab.dao.EquipmentDao;
import com.barezen.lab.dao.LabDao;
import com.barezen.lab.entity.Lab;
import com.barezen.lab.exception.ServiceException;

import java.util.List;

/** 实验室业务：查询、管理员增删改；有设备时删除被拦截 */
public class LabService {

    private final LabDao labDao = new LabDao();
    private final EquipmentDao equipmentDao = new EquipmentDao();

    public List<Lab> list() {
        return labDao.findAll();
    }

    public Lab getById(Long id) {
        Lab lab = labDao.findById(id);
        if (lab == null) {
            throw new ServiceException("实验室不存在");
        }
        return lab;
    }

    public void add(Lab lab) {
        validate(lab);
        labDao.save(lab);
    }

    public void update(Lab lab) {
        if (lab.getId() == null) {
            throw new ServiceException("实验室 ID 不能为空");
        }
        validate(lab);
        labDao.update(lab);
    }

    /** 删除实验室：真实业务规则——实验室下还有设备时不允许删除 */
    public void delete(Long id) {
        Lab lab = labDao.findById(id);
        if (lab == null) {
            throw new ServiceException("实验室不存在");
        }
        if (lab.getEquipmentCount() > 0) {
            throw new ServiceException("实验室下还有 " + lab.getEquipmentCount() + " 台设备，不能删除");
        }
        labDao.deleteById(id);
    }

    private void validate(Lab lab) {
        if (lab.getName() == null || lab.getName().trim().isEmpty()) {
            throw new ServiceException("实验室名称不能为空");
        }
    }
}
