package com.barezen.lab.dao;

import com.barezen.lab.entity.Lab;
import com.barezen.lab.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/** 实验室 DAO：JDBC PreparedStatement 访问 labs 表（子查询统计设备数） */
public class LabDao {

    public List<Lab> findAll() {
        String sql = "SELECT l.*, (SELECT COUNT(*) FROM equipments e WHERE e.lab_id = l.id) AS equipment_count "
                + "FROM labs l ORDER BY l.id";
        Connection conn = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        List<Lab> list = new ArrayList<>();
        try {
            conn = DBUtil.getConnection();
            st = conn.prepareStatement(sql);
            rs = st.executeQuery();
            while (rs.next()) {
                list.add(map(rs));
            }
            return list;
        } catch (Exception e) {
            throw new RuntimeException("查询实验室列表失败", e);
        } finally {
            DBUtil.close(rs, st, conn);
        }
    }

    public Lab findById(Long id) {
        String sql = "SELECT l.*, (SELECT COUNT(*) FROM equipments e WHERE e.lab_id = l.id) AS equipment_count "
                + "FROM labs l WHERE l.id = ?";
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
            throw new RuntimeException("查询实验室失败", e);
        } finally {
            DBUtil.close(rs, st, conn);
        }
    }

    public void save(Lab lab) {
        String sql = "INSERT INTO labs (name, location) VALUES (?, ?)";
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = DBUtil.getConnection();
            st = conn.prepareStatement(sql);
            st.setString(1, lab.getName());
            st.setString(2, lab.getLocation());
            st.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("新增实验室失败", e);
        } finally {
            DBUtil.close(null, st, conn);
        }
    }

    public void update(Lab lab) {
        String sql = "UPDATE labs SET name = ?, location = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = DBUtil.getConnection();
            st = conn.prepareStatement(sql);
            st.setString(1, lab.getName());
            st.setString(2, lab.getLocation());
            st.setLong(3, lab.getId());
            st.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("修改实验室失败", e);
        } finally {
            DBUtil.close(null, st, conn);
        }
    }

    public void deleteById(Long id) {
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = DBUtil.getConnection();
            st = conn.prepareStatement("DELETE FROM labs WHERE id = ?");
            st.setLong(1, id);
            st.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("删除实验室失败", e);
        } finally {
            DBUtil.close(null, st, conn);
        }
    }

    private Lab map(ResultSet rs) throws Exception {
        Lab l = new Lab();
        l.setId(rs.getLong("id"));
        l.setName(rs.getString("name"));
        l.setLocation(rs.getString("location"));
        l.setCreatedAt(rs.getTimestamp("created_at"));
        l.setEquipmentCount(rs.getInt("equipment_count"));
        return l;
    }
}
