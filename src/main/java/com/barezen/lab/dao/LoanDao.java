package com.barezen.lab.dao;

import com.barezen.lab.entity.Loan;
import com.barezen.lab.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/** 借用记录 DAO：JDBC PreparedStatement 访问 loans 表（联 users / equipments 取名称） */
public class LoanDao {

    /** 常用列片段：联表取借用人用户名/姓名与设备名称/编号 */
    private static final String SELECT_SQL =
            "SELECT l.*, u.username, u.name AS user_name, e.name AS equipment_name, e.code AS equipment_code "
            + "FROM loans l "
            + "JOIN users u ON l.user_id = u.id "
            + "JOIN equipments e ON l.equipment_id = e.id ";

    public List<Loan> findByUser(Long userId) {
        return query(SELECT_SQL + "WHERE l.user_id = ? ORDER BY l.id DESC", userId);
    }

    public List<Loan> findAll() {
        return query(SELECT_SQL + "ORDER BY l.id DESC", null);
    }

    /** 逾期列表：借用中且已过应还时间 */
    public List<Loan> findOverdue() {
        return query(SELECT_SQL + "WHERE l.status = 'ACTIVE' AND l.due_time < NOW() ORDER BY l.due_time", null);
    }

    public List<Loan> findRecent(int limit) {
        return query(SELECT_SQL + "ORDER BY l.id DESC LIMIT " + limit, null);
    }

    public Loan findById(Long id) {
        List<Loan> list = query(SELECT_SQL + "WHERE l.id = ?", id);
        return list.isEmpty() ? null : list.get(0);
    }

    public void save(Loan loan) {
        String sql = "INSERT INTO loans (user_id, equipment_id, borrow_time, due_time, purpose, status) VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = DBUtil.getConnection();
            st = conn.prepareStatement(sql);
            st.setLong(1, loan.getUserId());
            st.setLong(2, loan.getEquipmentId());
            st.setTimestamp(3, loan.getBorrowTime());
            st.setTimestamp(4, loan.getDueTime());
            st.setString(5, loan.getPurpose());
            st.setString(6, loan.getStatus());
            st.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("新增借用记录失败", e);
        } finally {
            DBUtil.close(null, st, conn);
        }
    }

    /** 归还：写归还时间，并把状态置为 RETURNED */
    public void returnLoan(Long id) {
        String sql = "UPDATE loans SET return_time = NOW(), status = 'RETURNED' WHERE id = ? AND return_time IS NULL";
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = DBUtil.getConnection();
            st = conn.prepareStatement(sql);
            st.setLong(1, id);
            st.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("归还失败", e);
        } finally {
            DBUtil.close(null, st, conn);
        }
    }

    /** 统计类查询：借用中 / 逾期数量 */
    public int countActive() {
        return count("WHERE l.status = 'ACTIVE'");
    }

    public int countOverdue() {
        return count("WHERE l.status = 'ACTIVE' AND l.due_time < NOW()");
    }

    private int count(String where) {
        Connection conn = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            st = conn.prepareStatement("SELECT COUNT(*) FROM loans l " + where);
            rs = st.executeQuery();
            rs.next();
            return rs.getInt(1);
        } catch (Exception e) {
            throw new RuntimeException("统计借用记录失败", e);
        } finally {
            DBUtil.close(rs, st, conn);
        }
    }

    private List<Loan> query(String sql, Long param) {
        Connection conn = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        List<Loan> list = new ArrayList<>();
        try {
            conn = DBUtil.getConnection();
            st = conn.prepareStatement(sql);
            if (param != null) {
                st.setLong(1, param);
            }
            rs = st.executeQuery();
            while (rs.next()) {
                list.add(map(rs));
            }
            return list;
        } catch (Exception e) {
            throw new RuntimeException("查询借用记录失败", e);
        } finally {
            DBUtil.close(rs, st, conn);
        }
    }

    private Loan map(ResultSet rs) throws Exception {
        Loan l = new Loan();
        l.setId(rs.getLong("id"));
        l.setUserId(rs.getLong("user_id"));
        l.setUsername(rs.getString("username"));
        l.setUserName(rs.getString("user_name"));
        l.setEquipmentId(rs.getLong("equipment_id"));
        l.setEquipmentName(rs.getString("equipment_name"));
        l.setEquipmentCode(rs.getString("equipment_code"));
        l.setPurpose(rs.getString("purpose"));
        l.setBorrowTime(rs.getTimestamp("borrow_time"));
        l.setDueTime(rs.getTimestamp("due_time"));
        l.setReturnTime(rs.getTimestamp("return_time"));
        l.setStatus(rs.getString("status"));
        l.setCreatedAt(rs.getTimestamp("created_at"));
        return l;
    }
}
