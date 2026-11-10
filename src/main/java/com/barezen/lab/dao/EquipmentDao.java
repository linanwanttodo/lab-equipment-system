package com.barezen.lab.dao;

import com.barezen.lab.entity.Equipment;
import com.barezen.lab.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/** 设备 DAO：JDBC PreparedStatement 访问 equipments 表（联 labs 取实验室名） */
public class EquipmentDao {

    /** 动态条件查询：实验室 / 类别 / 状态 / 关键字（ PreparedStatement 参数化，无 SQL 注入） */
    public List<Equipment> search(Long labId, String category, String status, String keyword) {
        StringBuilder sql = new StringBuilder(
                "SELECT e.*, l.name AS lab_name FROM equipments e LEFT JOIN labs l ON e.lab_id = l.id WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (labId != null) {
            sql.append(" AND e.lab_id = ?");
            params.add(labId);
        }
        if (category != null && !category.isEmpty()) {
            sql.append(" AND e.category = ?");
            params.add(category);
        }
        if (status != null && !status.isEmpty()) {
            sql.append(" AND e.status = ?");
            params.add(status);
        }
        if (keyword != null && !keyword.isEmpty()) {
            sql.append(" AND (e.name LIKE ? OR e.description LIKE ?)");
            params.add("%" + keyword + "%");
            params.add("%" + keyword + "%");
        }
        sql.append(" ORDER BY e.id");
        Connection conn = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        List<Equipment> list = new ArrayList<>();
        try {
            conn = DBUtil.getConnection();
            st = conn.prepareStatement(sql.toString());
            for (int i = 0; i < params.size(); i++) {
                st.setObject(i + 1, params.get(i));
            }
            rs = st.executeQuery();
            while (rs.next()) {
                list.add(map(rs));
            }
            return list;
        } catch (Exception e) {
            throw new RuntimeException("查询设备列表失败", e);
        } finally {
            DBUtil.close(rs, st, conn);
        }
    }

    public Equipment findById(Long id) {
        String sql = "SELECT e.*, l.name AS lab_name FROM equipments e LEFT JOIN labs l ON e.lab_id = l.id WHERE e.id = ?";
        Connection conn = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            st = conn.prepareStatement(sql);
            st.setLong(1, id);
            rs = st.executeQuery();
            return rs.next() ? map(rs) : null;
        } catch (Exception e) {
            throw new RuntimeException("查询设备失败", e);
        } finally {
            DBUtil.close(rs, st, conn);
        }
    }

    public List<String> findAllCategories() {
        String sql = "SELECT DISTINCT category FROM equipments ORDER BY category";
        Connection conn = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        List<String> list = new ArrayList<>();
        try {
            conn = DBUtil.getConnection();
            st = conn.prepareStatement(sql);
            rs = st.executeQuery();
            while (rs.next()) {
                list.add(rs.getString(1));
            }
            return list;
        } catch (Exception e) {
            throw new RuntimeException("查询设备类别失败", e);
        } finally {
            DBUtil.close(rs, st, conn);
        }
    }

    public void save(Equipment e) {
        String sql = "INSERT INTO equipments (code, lab_id, name, category, status, total, available, deposit, description) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = DBUtil.getConnection();
            st = conn.prepareStatement(sql);
            fill(st, e);
            st.executeUpdate();
        } catch (Exception ex) {
            throw new RuntimeException("新增设备失败", ex);
        } finally {
            DBUtil.close(null, st, conn);
        }
    }

    public void update(Equipment e) {
        String sql = "UPDATE equipments SET code = ?, lab_id = ?, name = ?, category = ?, status = ?, total = ?, available = ?, deposit = ?, description = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = DBUtil.getConnection();
            st = conn.prepareStatement(sql);
            int idx = fill(st, e);
            st.setLong(idx, e.getId());
            st.executeUpdate();
        } catch (Exception ex) {
            throw new RuntimeException("修改设备失败", ex);
        } finally {
            DBUtil.close(null, st, conn);
        }
    }

    public void deleteById(Long id) {
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = DBUtil.getConnection();
            st = conn.prepareStatement("DELETE FROM equipments WHERE id = ?");
            st.setLong(1, id);
            st.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("删除设备失败", e);
        } finally {
            DBUtil.close(null, st, conn);
        }
    }

    /** 库存增减：delta 为正恢复库存，为负扣减库存；可借为 0 时状态置维护中 */
    public void changeAvailable(Long id, int delta) {
        String sql = "UPDATE equipments SET available = available + ?, "
                + "status = CASE WHEN available + ? > 0 THEN 'AVAILABLE' ELSE 'MAINTENANCE' END WHERE id = ?";
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = DBUtil.getConnection();
            st = conn.prepareStatement(sql);
            st.setInt(1, delta);
            st.setInt(2, delta);
            st.setLong(3, id);
            st.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("更新设备库存失败", e);
        } finally {
            DBUtil.close(null, st, conn);
        }
    }

    private int fill(PreparedStatement st, Equipment e) throws Exception {
        int idx = 1;
        st.setString(idx++, e.getCode());
        if (e.getLabId() == null) {
            st.setNull(idx++, java.sql.Types.BIGINT);
        } else {
            st.setLong(idx++, e.getLabId());
        }
        st.setString(idx++, e.getName());
        st.setString(idx++, e.getCategory());
        st.setString(idx++, e.getStatus());
        st.setInt(idx++, e.getTotal());
        st.setInt(idx++, e.getAvailable());
        st.setDouble(idx++, e.getDeposit());
        st.setString(idx++, e.getDescription());
        return idx;
    }

    private Equipment map(ResultSet rs) throws Exception {
        Equipment e = new Equipment();
        e.setId(rs.getLong("id"));
        e.setCode(rs.getString("code"));
        e.setLabId(rs.getObject("lab_id") == null ? null : rs.getLong("lab_id"));
        e.setLabName(rs.getString("lab_name"));
        e.setName(rs.getString("name"));
        e.setCategory(rs.getString("category"));
        e.setStatus(rs.getString("status"));
        e.setTotal(rs.getInt("total"));
        e.setAvailable(rs.getInt("available"));
        e.setDeposit(rs.getDouble("deposit"));
        e.setDescription(rs.getString("description"));
        e.setCreatedAt(rs.getTimestamp("created_at"));
        return e;
    }
}
