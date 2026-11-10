package com.barezen.lab.dao;

import com.barezen.lab.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** 统计 DAO：后台总览聚合查询（管理员） */
public class StatsDao {

    public int countUsers() {
        return scalar("SELECT COUNT(*) FROM users");
    }

    public int countEquipments() {
        return scalar("SELECT COUNT(*) FROM equipments");
    }

    public int countLabs() {
        return scalar("SELECT COUNT(*) FROM labs");
    }

    public int sumAvailable() {
        return scalar("SELECT COALESCE(SUM(available), 0) FROM equipments");
    }

    public int countLoans() {
        return scalar("SELECT COUNT(*) FROM loans");
    }

    public int countReturned() {
        return scalar("SELECT COUNT(*) FROM loans WHERE return_time IS NOT NULL");
    }

    public int countBorrowed() {
        return scalar("SELECT COALESCE(SUM(total - available), 0) FROM equipments");
    }

    /** 类别分布：按台数聚合 */
    public Map<String, Integer> categoryDistribution() {
        String sql = "SELECT category, COUNT(*) AS cnt FROM equipments GROUP BY category ORDER BY category";
        Connection conn = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        Map<String, Integer> map = new LinkedHashMap<>();
        try {
            conn = DBUtil.getConnection();
            st = conn.prepareStatement(sql);
            rs = st.executeQuery();
            while (rs.next()) {
                map.put(rs.getString(1), rs.getInt(2));
            }
            return map;
        } catch (Exception e) {
            throw new RuntimeException("统计类别分布失败", e);
        } finally {
            DBUtil.close(rs, st, conn);
        }
    }

    /** 实验室设备分布 */
    public Map<String, Integer> labDistribution() {
        String sql = "SELECT l.name, COUNT(e.id) FROM labs l LEFT JOIN equipments e ON e.lab_id = l.id "
                + "GROUP BY l.id, l.name ORDER BY l.id";
        Connection conn = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        Map<String, Integer> map = new LinkedHashMap<>();
        try {
            conn = DBUtil.getConnection();
            st = conn.prepareStatement(sql);
            rs = st.executeQuery();
            while (rs.next()) {
                map.put(rs.getString(1), rs.getInt(2));
            }
            return map;
        } catch (Exception e) {
            throw new RuntimeException("统计实验室分布失败", e);
        } finally {
            DBUtil.close(rs, st, conn);
        }
    }

    private int scalar(String sql) {
        Connection conn = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            st = conn.prepareStatement(sql);
            rs = st.executeQuery();
            rs.next();
            return rs.getInt(1);
        } catch (Exception e) {
            throw new RuntimeException("统计失败", e);
        } finally {
            DBUtil.close(rs, st, conn);
        }
    }
}
